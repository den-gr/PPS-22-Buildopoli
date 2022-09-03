package behaviour

import behaviour.BehaviourModule.{Behaviour, Index}
import behaviour.event.EventGroup

import scala.collection.mutable

/** Allows correctly navigate between event groups and their successors of a Behaviour
  */
trait BehaviourIterator:
  /** @return
    *   false if there are not available EventGroup
    */
  def hasNext: Boolean

  /** Choice of next event
    * @param index
    *   is a double tuple where first element is event group index, and second is an event index
    */
  def next(index: Index = (0,0)): Unit

  /** @return
    *   current, available to the player, sequence of events
    */
  def currentEvents: Seq[EventGroup]

object BehaviourIterator:
  def apply(events: Seq[EventGroup], playerId: Int): BehaviourIterator =
    if events.count(_.isAtomic) > 1 then throw IllegalStateException("Only one event group can be atomic")
    BehaviourIteratorImpl(events, playerId)

  private case class BehaviourIteratorImpl(events: Seq[EventGroup], playerId: Int) extends BehaviourIterator:
    val eventStack: mutable.Stack[Seq[EventGroup]] = mutable.Stack(events)

    override def hasNext: Boolean = eventStack.nonEmpty

    override def next(index: Index ): Unit =
      import behaviour.BehaviourModule.*
      if index._1 < 0 || index._1 >= this.currentEvents.length then
        throw IllegalArgumentException(s"Chose indexes point to a not existing event. -> $index")
      val groups = eventStack.pop()
      val newGroup = chooseEvent(groups(index._1))(playerId, index._2)

      if eventStack.nonEmpty && eventStack.last(index._1).isAtomic then eventStack.push(groups.patch(index._1, Nil, 1))
      if newGroup.nonEmpty then eventStack.push(Seq(newGroup.get))

    override def currentEvents: Seq[EventGroup] = eventStack.last

  private def chooseEvent(currentEvents: Seq[EventGroup])(playerId: Int, index: (Int, Int)): Seq[EventGroup] =
    try
      val nextOpEvents: Option[EventGroup] = chooseEvent(currentEvents(index._1))(playerId, index._2)

      // remove chose EventGroup
      var newEvents = currentEvents.patch(index._1, Nil, 1)

      // insert next EventGroup
      if nextOpEvents.nonEmpty then
        val nextEventGroup: EventGroup = EventGroup(
          for
            ev <- nextOpEvents.get
            if ev.hasToRun(playerId)
          yield ev,
          nextOpEvents.get.isAtomic
        )
        newEvents = newEvents :+ nextEventGroup
      newEvents
    catch
      case _: IndexOutOfBoundsException =>
        throw IllegalArgumentException("Chose indexes point to a not existing event. -> " + index)

  private def chooseEvent(eventGroup: EventGroup)(playerId: Int, index: Int): Option[EventGroup] =
    try
      val event = eventGroup(index)
      event.run(playerId)
      event.nextEvent
    catch
      case _: IndexOutOfBoundsException =>
        throw IllegalArgumentException("Chose index of a not existing event. -> " + index)
