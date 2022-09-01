package behaviour

import behaviour.BehaviourModule.{Behaviour, Index}
import behaviour.event.EventGroup

import scala.collection.mutable

trait BehaviourIterator:
  def hasNext: Boolean
  def choose(index: Index): Unit
  def next: Seq[EventGroup]

object BehaviourIterator:
  def apply(events: Seq[EventGroup], playerId: Int): BehaviourIterator =
    if events.count(_.isAtomic) > 1 then throw IllegalStateException("Only one event group can be atomic")
    BehaviourIteratorImpl(events, playerId)

  private case class BehaviourIteratorImpl(events: Seq[EventGroup], playerId: Int) extends BehaviourIterator:
    val eventStack: mutable.Stack[Seq[EventGroup]] = mutable.Stack(events)

    def hasNext: Boolean = eventStack.nonEmpty

    def choose(index: Index): Unit =
      import behaviour.BehaviourModule.*

      val groups = eventStack.pop()
      val newGroup = chooseEvent(groups(index._1))(playerId, index._2)
      if eventStack.nonEmpty && eventStack.last(index._1).isAtomic then eventStack.push(groups.patch(index._1, Nil, 1))
      if newGroup.nonEmpty then eventStack.push(Seq(newGroup.get))

    def next: Seq[EventGroup] = eventStack.last
