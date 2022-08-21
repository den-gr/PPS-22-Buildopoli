package event

import scala.annotation.targetName

object EventModule:
  trait EventStory:
    def description: String
    def choices: Seq[String]
    def isSingleAction: Boolean = choices.length == 1

  trait StoryAction:
    evSt: EventStory =>
    def actions: Seq[() => Unit]
    def storyActions: Seq[(String, Action)] =
      if choices.length != actions.length then
        throw IllegalStateException("Each description must have a corresponding action")
      for
        choice <- choices
        action <- actions
      yield (choice, action)

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
  type Action = () => Unit

  trait Scenario:
    def eventStrategy: EventStrategy
    def eventStory(playerId: Int): EventStory

  object EventStory:
    val MAIN_ACTION = 0
    def apply(desc: String, choices: Seq[String]): EventStory = EventStoryImpl(desc, choices)

    class EventStoryImpl(
        override val description: String,
        override val choices: Seq[String]
    ) extends EventStory:
      override def toString: String =
        s"$description \n\t" + choices.mkString("\n\t")

    class EventStoryActionsImpl(description: String, choices: Seq[String], override val actions: Seq[Action])
        extends EventStoryImpl(description, choices)
        with StoryAction

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
        // TODO the code is fragile

  object EventOperation:
    extension [T <: Event](e: T)
      @targetName("append")
      def ++(nextEvent: T): T = e.doCopy(Some(nextEvent)).asInstanceOf[T]

  object EventFactory:
    import Event.*
    def InfoEvent(story: EventStory, condition: EventPrecondition): ConditionalEvent = Event(Scenario(story), condition)
