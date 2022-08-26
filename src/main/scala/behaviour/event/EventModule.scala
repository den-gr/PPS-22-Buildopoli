package behaviour.event

import java.awt.Choice
import scala.annotation.targetName
import behaviour.event.EventStoryModule
import behaviour.event.EventStoryModule.*

object EventModule:
  type EventStrategy = Int => Unit
  type EventPrecondition = Int => Boolean
  type StoryGenerator = Int => EventStory

  trait EventGroup extends Seq[Event]:
    val events: Seq[Event]
    export events.*
    val isAtomic: Boolean
    // TODO def isMandatory: Boolean

  object EventGroup:
    def apply(elems: Event*): EventGroup = EventGroupImpl(elems)
    def apply(elems: Seq[Event], isAtomic: Boolean): EventGroup = EventGroupImpl(elems, isAtomic)

    class EventGroupImpl(override val events: Seq[Event], override val isAtomic: Boolean = false) extends EventGroup

  trait Event:
    def nextEvent: Option[EventGroup]
    def run(playerId: Int): Unit
    def eventStory(playerId: Int): EventStory
    def doCopy(nextEv: Option[EventGroup]): Event
    def hasToRun(playerId: Int): Boolean

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
        nextEvent: Option[EventGroup] = None
    ): Event =
      EventImpl(scenario, condition, nextEvent)

    def apply(scenario: Scenario, nextEvent: Option[EventGroup]): Event =
      EventImpl(scenario, WITHOUT_PRECONDITION, nextEvent)

    def apply(scenario: Scenario): Event = apply(scenario, None)

    case class EventImpl(scenario: Scenario, condition: EventPrecondition, nextEvent: Option[EventGroup]) extends Event:

      override def run(playerId: Int): Unit =
        scenario.eventStrategy(playerId)

      override def eventStory(playerId: Int): EventStory = scenario.eventStory(playerId)

      override def doCopy(nextEv: Option[EventGroup]): Event = EventImpl(scenario, condition, nextEv)

      override def hasToRun(playerId: Int): Boolean = condition(playerId)

  object EventOperation:
    extension [T <: Event](e: T)
      @targetName("append")
      def ++(nextEvent: T): T =
        if e.getClass != nextEvent.getClass then
          throw new IllegalArgumentException("Both event must be of the same type")
        e.doCopy(Some(EventGroup(nextEvent))).asInstanceOf[T]
