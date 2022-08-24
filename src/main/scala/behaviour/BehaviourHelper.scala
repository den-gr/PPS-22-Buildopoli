package behaviour

import behaviour.BehaviourModule.Behaviour
import behaviour.event.EventModule.EventGroup

object BehaviourHelper {
  trait BehaviourIterator extends Iterator[Seq[EventGroup]]
  
  class BehaviourIteratorImpl(val events : Seq[EventGroup], val playerId : Int) extends BehaviourIterator:
    
    override def hasNext: Boolean = ???

    override def next(): Seq[EventGroup] = ???


}
