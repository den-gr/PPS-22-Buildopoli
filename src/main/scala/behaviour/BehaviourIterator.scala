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
  def next(index: Index = (0, 0)): Unit

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

    override def next(index: Index): Unit =
      val groups = eventStack.pop()
      index match
        case (groupIndex: Int, eventIndex: Int)
            if groupIndex < 0 || groupIndex >= groups.length || eventIndex >= groups(groupIndex).length =>
          eventStack.push(groups) // redo pop
          throw IllegalArgumentException(s"Chose indexes point to a not existing event. -> $index")

        case (groupIndex: Int, eventIndex: Int) =>
          val newGroup = chooseEvent(groups(groupIndex))(playerId, eventIndex)
          if groups(groupIndex).isAtomic then
            eventStack.push(groups.patch(groupIndex, Nil, 1))
            if newGroup.nonEmpty then eventStack.push(Seq(newGroup.get))
          else if newGroup.nonEmpty then eventStack.push(newGroup.get +: groups.patch(groupIndex, Nil, 1))

    override def currentEvents: Seq[EventGroup] = eventStack.head

  private def chooseEvent(eventGroup: EventGroup)(playerId: Int, index: Int): Option[EventGroup] =
    try
      val event = eventGroup(index)
      event.run(playerId)
      event.nextEvent
    catch
      case _: IndexOutOfBoundsException =>
        throw IllegalArgumentException("Chose index of a not existing event. -> " + index)
