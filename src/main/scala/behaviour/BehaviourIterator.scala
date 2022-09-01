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
  def next(index: Index): Unit

  /** @return
    *   current, available to the player, sequence of events
    */
  def current: Seq[EventGroup]

object BehaviourIterator:
  def apply(events: Seq[EventGroup], playerId: Int): BehaviourIterator =
    if events.count(_.isAtomic) > 1 then throw IllegalStateException("Only one event group can be atomic")
    BehaviourIteratorImpl(events, playerId)

  private case class BehaviourIteratorImpl(events: Seq[EventGroup], playerId: Int) extends BehaviourIterator:
    val eventStack: mutable.Stack[Seq[EventGroup]] = mutable.Stack(events)

    def hasNext: Boolean = eventStack.nonEmpty

    def next(index: Index): Unit =
      import behaviour.BehaviourModule.*
      if index._1 < 0 || index._1 >= this.current.length then
        throw IllegalArgumentException(s"Chose indexes point to a not existing event. -> $index")
      val groups = eventStack.pop()
      val newGroup = chooseEvent(groups(index._1))(playerId, index._2)

      if eventStack.nonEmpty && eventStack.last(index._1).isAtomic then eventStack.push(groups.patch(index._1, Nil, 1))
      if newGroup.nonEmpty then eventStack.push(Seq(newGroup.get))

    def current: Seq[EventGroup] = eventStack.last
