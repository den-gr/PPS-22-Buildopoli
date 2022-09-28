package lib.behaviour.event

import EventModule.Event

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

  /** When it is true a player must not to be able to end his turn until this event group is present in behaviour
    * explorer
    */
  val isMandatory: Boolean

  /** Create a new EventGroup with new events but old setup
    * @param events
    *   events of new event group
    * @return
    *   an new event group
    */
  def replaceEvents(events: Seq[Event]): EventGroup

object EventGroup:

  /** Constructor of a not atomic [[EventGroup]]
    *
    * @param elems
    *   events of event group
    * @return
    *   an instantiated event group
    */
  def apply(elems: Event*): EventGroup = EventGroupImpl(elems, false, false)

  /** Constructor of an [[EventGroup]]
    *
    * @param elems
    *   events of event group
    * @param isAtomic
    *   define if event chain of this event group is atomic
    * @param isMandatory
    *   true if player can not start new turn when this event group is not explored
    * @return
    *   an instantiated event group
    */
  def apply(elems: Seq[Event], isAtomic: Boolean = false, isMandatory: Boolean = false): EventGroup =
    EventGroupImpl(elems, isAtomic, isMandatory)

  private case class EventGroupImpl(
      override val events: Seq[Event],
      override val isAtomic: Boolean,
      override val isMandatory: Boolean
  ) extends EventGroup:
    override def replaceEvents(events: Seq[Event]): EventGroup = EventGroup(events, this.isAtomic, this.isMandatory)
