package event

import scala.annotation.targetName

object EventModule:
  trait EventStory:
    def description: String
    def actions: Seq[String]
    def isSingleAction: Boolean = actions.length == 1

  trait StoryAction:
    evSt: EventStory =>
    def storyActions: Seq[(String, () => Unit)]

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
    def apply(scenario: Scenario, condition: EventPrecondition): ConditionalEvent =
      ConditionalEventImpl(scenario, condition)

    case class EventImpl(scenario: Scenario, nextEvent: Option[Event] = None) extends Event:

      override def run(playerId: Int): Unit =
        scenario.eventStrategy(playerId)

      override def eventStory(playerId: Int): EventStory = scenario.eventStory(playerId)

      override def doCopy(nextEv: Option[Event]): Event = EventImpl(scenario, nextEv)

    class ConditionalEventImpl(
        scenario: Scenario,
        condition: EventPrecondition,
        override val nextEvent: Option[ConditionalEvent] = None
    ) extends EventImpl(scenario),
          ConditionalEvent:

      override def hasToRun(playerId: Int): Boolean = condition(playerId)

      override def doCopy(next: Option[Event]): ConditionalEvent = next.getOrElse(None) match
        case event: ConditionalEvent => ConditionalEventImpl(scenario, condition, Some(event))
        case None => ConditionalEventImpl(scenario, condition, None)
        case _ => throw IllegalArgumentException("Event must be instance of ConditionalEvent")

  object EventOperation:
    extension (e: Event)
      @targetName("append")
      def ++(nextEvent: Event): Event = e.doCopy(Some(nextEvent))

    extension (e: ConditionalEvent)
      @targetName("append")
      def ++(nextEvent: ConditionalEvent): ConditionalEvent = e.doCopy(Some(nextEvent))

  object EventFactory:
    import Event.*
    def InfoEvent(story: EventStory, condition: EventPrecondition): ConditionalEvent = Event(Scenario(story), condition)
