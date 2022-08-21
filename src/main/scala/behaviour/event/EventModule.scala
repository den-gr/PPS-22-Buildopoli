package behaviour.event

import java.awt.Choice
import scala.annotation.targetName


object EventModule:
  import behaviour.event.EventStoryModule
  import behaviour.event.EventStoryModule.*
  trait Event:
    def nextEvent: Option[Event]
    def run(playerId: Int): Unit
    def eventStory(playerId: Int): EventStory
    def doCopy(nextEv: Option[Event]): Event

  trait Condition[T]:
    def hasToRun(playerId: T): Boolean

  trait ConditionalEvent extends Event with Condition[Int]:
    override def nextEvent: Option[ConditionalEvent]
    override def doCopy(nextEvent: Option[Event]): ConditionalEvent

  type EventStrategy = Int => Unit
  type EventPrecondition = Int => Boolean
  type StoryGenerator = Int => EventStory

  trait Scenario:
    def eventStrategy: EventStrategy
    def eventStory(playerId: Int): EventStory

  

  object Scenario:
    import EventStory.*
    val tempStory: EventStory = EventStory("My temp description", List("OK"))

    def apply(story: EventStory): Scenario = ScenarioImpl(storyGenerator = _ => story)

    def apply(eventStrategy: EventStrategy, story: EventStory): Scenario =
      ScenarioImpl(eventStrategy, _ => story)

    def apply(eventStrategy: EventStrategy, storyGenerator: StoryGenerator): Scenario =
      ScenarioImpl(eventStrategy, storyGenerator)

    def apply(storyGenerator: StoryGenerator): Scenario = ScenarioImpl(storyGenerator = storyGenerator)

    case class ScenarioImpl(
        override val eventStrategy: EventStrategy = _ => (),
        storyGenerator: StoryGenerator
    ) extends Scenario:
      override def eventStory(playerId: Int): EventStory = storyGenerator(playerId)

  object Event:
    val WITHOUT_PRECONDITION: EventPrecondition = _ => true
    def apply(
        scenario: Scenario,
        condition: EventPrecondition,
        nextEvent: Option[ConditionalEvent] = None
    ): ConditionalEvent =
      ConditionalEventImpl(scenario, condition, nextEvent)

    def apply(scenario: Scenario, nextEvent: Option[Event]): Event = EventImpl(scenario, nextEvent)

    def apply(scenario: Scenario): Event = apply(scenario, None)

    case class EventImpl(scenario: Scenario, nextEvent: Option[Event]) extends Event:

      override def run(playerId: Int): Unit =
        scenario.eventStrategy(playerId)

      override def eventStory(playerId: Int): EventStory = scenario.eventStory(playerId)

      override def doCopy(nextEv: Option[Event]): Event = EventImpl(scenario, nextEv)

    class ConditionalEventImpl(
        scenario: Scenario,
        condition: EventPrecondition,
        override val nextEvent: Option[ConditionalEvent]
    ) extends EventImpl(scenario, nextEvent),
          ConditionalEvent:

      override def hasToRun(playerId: Int): Boolean = condition(playerId)

      override def doCopy(next: Option[Event]): ConditionalEvent = next.getOrElse(None) match
        case event: ConditionalEvent => ConditionalEventImpl(scenario, condition, Some(event))
        case None => ConditionalEventImpl(scenario, condition, None)
        case _ => throw IllegalArgumentException("Event must be instance of ConditionalEvent")
        // TODO the code is fragile

  object EventOperation:
    extension [T <: Event](e: T)
      @targetName("append")
      def ++(nextEvent: T): T =
        if e.getClass != nextEvent.getClass then
          throw new IllegalArgumentException("Both event must be of the same type")
        e.doCopy(Some(nextEvent)).asInstanceOf[T]

  object EventFactory:
    import Event.*
    def InfoEvent(story: EventStory, condition: EventPrecondition): ConditionalEvent = Event(Scenario(story), condition)
