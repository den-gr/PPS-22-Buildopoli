package behaviour

import behaviour.BehaviourModule.{Behaviour, Index}
import behaviour.event.EventModule.EventGroup

import scala.collection.mutable

object BehaviourHelper:

  class BehaviourIteratorImpl(val events: Seq[EventGroup], val playerId: Int):
    var eventStack: mutable.Stack[Seq[EventGroup]] = mutable.Stack()

    if events.count(_.isAtomic) > 1 then throw IllegalStateException("Only one event group can be atomic")
    eventStack.push(events)

    def hasNext: Boolean = eventStack.isEmpty

    def choose(index: Index): Unit =
      import behaviour.BehaviourModule.*
      if eventStack.last(index._1).isAtomic then
        val groups = eventStack.pop()
        val newGroup = chooseEvent(groups(index._1))(playerId, index._2)
        eventStack.push(groups.patch(index._1, Nil, 1))
        if newGroup.nonEmpty then eventStack.push(Seq(newGroup.get))

    def next(): Seq[EventGroup] = eventStack.last
