package behaviour
import behaviour.BehaviourModule.StoryGroup
import behaviour.event.EventModule
import behaviour.event.EventModule.*
import behaviour.event.EventGroup
import behaviour.event.EventStoryModule.EventStory

import scala.annotation.targetName

object BehaviourModule extends StoryConverter:

  type StoryGroup = Seq[EventStory]

  /** A choose of an event of the behaviour. It is a tuple2: (eventGroupIndex, eventIndex)
    */
  type Index = (Int, Int)

  trait Behaviour:
    def getInitialEvents(playerId: Int): Seq[EventGroup]

  object Behaviour:
    def apply(initialEvents: Seq[EventGroup]): Behaviour = BehaviourImpl(initialEvents)
    def apply(singleEventGroup: EventGroup): Behaviour = apply(Seq(singleEventGroup))
    @targetName("Constructor with events of a simple single event group")
    def apply(eventsOfSingleEventGroup: Event*): Behaviour = apply(EventGroup(eventsOfSingleEventGroup))

    private case class BehaviourImpl(private val initialEvents: Seq[EventGroup]) extends Behaviour:
      override def getInitialEvents(playerId: Int): Seq[EventGroup] =
        initialEvents
          .map(gr => EventGroup(gr.filter(_.hasToRun(playerId)), gr.isAtomic))
          .filter(_.nonEmpty)

  def chooseEvent(currentEvents: Seq[EventGroup])(playerId: Int, index: (Int, Int)): Seq[EventGroup] =
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

  def chooseEvent(eventGroup: EventGroup)(playerId: Int, index: Int): Option[EventGroup] =
    try
      val event = eventGroup(index)
      event.run(playerId)
      event.nextEvent
    catch
      case _: IndexOutOfBoundsException =>
        throw IllegalArgumentException("Chose index of a not existing event. -> " + index)
