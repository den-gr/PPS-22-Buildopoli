package behaviour.event

import behaviour.event.EventModule.Event

/** Sequence of events where only one can be chosen by player. EventGroup can be atomic in case when the whole chain of
  * events must be finished before starting choosing events from another event groups
  */
trait EventGroup extends Seq[Event]:
  val events: Seq[Event]
  export events.*

  /** When it is true to a players must not be allowed to choose events from another event groups until they finish
    * event chain of this event group
    */
  val isAtomic: Boolean

// TODO def isMandatory: Boolean

object EventGroup:
  
  /** Constructor of a not atomic [[EventGroup]]
    *
    * @param elems
    *   events of event group
    * @return
    *   an instantiated event group
    */
  def apply(elems: Event*): EventGroup = EventGroupImpl(elems, false)

  /** Constructor of an [[EventGroup]]
    *
    * @param elems
    *   events of event group
    * @param isAtomic
    *   define if event chain of this event group is atomic
    * @return
    *   an instantiated event group
    */
  def apply(elems: Seq[Event], isAtomic: Boolean = false): EventGroup = EventGroupImpl(elems, isAtomic)

  private class EventGroupImpl(override val events: Seq[Event], override val isAtomic: Boolean) extends EventGroup
