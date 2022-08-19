package event

import scala.annotation.targetName

object EventModule:
  trait EventStory:
    def description: String
    def actions: Seq[String]
    def isSingleAction: Boolean = actions.length == 1

  trait Event:
    var nextEvent: Option[Event]
    def run(playerId: Int): Unit
    def eventStory: EventStory
    def doCopy(): Event

  trait Condition[T]:
    def hasToRun(playerId: T): Boolean

  trait ConditionalEvent extends Event with Condition[Int]

  type EventStrategy = Int => Unit
  type EventPrecondition = Int => Boolean
  type StoryGenerator = () => EventStory

  trait Scenario:
    def eventStrategy: EventStrategy
    def eventStory: EventStory

  object EventStory:
    val MAIN_ACTION = 0
    def apply(desc: String, actions: Seq[String]): EventStory = EventStoryImpl(desc, actions)

    case class EventStoryImpl(
        override val description: String,
        override val actions: Seq[String]
    ) extends EventStory:
      override def toString: String =
        s"$description \n\t" + actions.mkString("\n\t")

  object Scenario:
    import EventStory.*
    val tempStory: EventStory = EventStory("My temp description", List("OK"))

    def apply(eventStrategy: EventStrategy): Scenario =
      ScenarioImpl(eventStrategy, () => tempStory)

    def apply(story: EventStory): Scenario = ScenarioImpl(storyGenerator = () => story)

    def apply(eventStrategy: EventStrategy, story: EventStory): Scenario =
      ScenarioImpl(eventStrategy, () => story)

    def apply(eventStrategy: EventStrategy, storyGenerator: StoryGenerator): Scenario =
      ScenarioImpl(eventStrategy, storyGenerator)

    def apply(storyGenerator: StoryGenerator): Scenario = ScenarioImpl(storyGenerator = storyGenerator)

    case class ScenarioImpl(
        override val eventStrategy: EventStrategy = _ => (),
        storyGenerator: StoryGenerator
    ) extends Scenario:
      override def eventStory: EventStory = storyGenerator()

  object Event:
    val WITHOUT_PRECONDITION: EventPrecondition = _ => true
    def apply(scenario: Scenario, condition: EventPrecondition): ConditionalEvent =
      ConditionalEventImpl(scenario, condition)

    case class EventImpl(scenario: Scenario) extends Event:
      var nextEvent: Option[Event] = None

      override def run(playerId: Int): Unit =
        scenario.eventStrategy(playerId)

      override def eventStory: EventStory = scenario.eventStory

      override def doCopy(): Event = EventImpl(scenario)

    class ConditionalEventImpl(scenario: Scenario, condition: EventPrecondition)
        extends EventImpl(scenario),
          ConditionalEvent:

      override def hasToRun(playerId: Int): Boolean = condition(playerId)

      override def doCopy(): Event = ConditionalEventImpl(scenario, condition)

  object EventOperation:
    extension (e: Event)
      @targetName("append")
      def ++(nextEvent: Event): Event =
        val copy = e.doCopy()
        copy.nextEvent = Some(nextEvent)
        copy
    extension (e: ConditionalEvent)
      @targetName("append")
      def ++(nextEvent: Event): ConditionalEvent =
        val copy = e.doCopy().asInstanceOf[ConditionalEvent]
        copy.nextEvent = Some(nextEvent)
        copy

  object EventFactory:
    import Event.*
    def InfoEvent(story: EventStory, condition: EventPrecondition): ConditionalEvent = Event(Scenario(story), condition)
