package event

object EventModule:
  trait EventStory:
    def description: String
    def actions: Seq[String]
    def isSingleAction: Boolean = actions.length == 1

  trait Event:
    def run(playerId: Int): Option[Event]
    def eventStory: EventStory

  trait Condition[T]:
    def hasToRun(playerId: T): Boolean

  trait ConditionalEvent extends Event with Condition[Int]

//  enum EventResult:
//    case OK
//    case INTERRUPT(msg: String)

//  type NextEvent = Option[Event]
  type EventStrategy = Int => Unit
  type EventPrecondition = Int => Boolean

  trait Scenario:
    def eventStrategy: EventStrategy
    def nextEvent: Option[Event]
    def eventStory: EventStory

  object EventStory:
    val MAIN_ACTION = 0
    def apply(desc: String, actions: Seq[String]): EventStory = EventStoryImpl(desc, actions)

    case class EventStoryImpl(
        override val description: String,
        override val actions: Seq[String]
    ) extends EventStory:
      override def toString: String =
        s"$description. Available actions: \n\t" + actions.mkString("\n\t")

  object Scenario:
    import EventStory.*
    val tempStory: EventStory = EventStory("My temp description", List("OK"))

    def apply(eventStrategy: EventStrategy, nextEvent: Option[Event]): Scenario =
      ScenarioImpl(eventStrategy, nextEvent, tempStory)

    def apply(nextEvent: Option[Event]): Scenario = ScenarioImpl(nextEvent = nextEvent, tempStory)

    def apply(eventStrategy: EventStrategy, nextEvent: Option[Event], story: EventStory): Scenario =
      ScenarioImpl(eventStrategy, nextEvent, story)

    case class ScenarioImpl(
        override val eventStrategy: EventStrategy = _ => (),
        override val nextEvent: Option[Event],
        override val eventStory: EventStory
    ) extends Scenario

  object Event:
    def apply(scenario: Scenario, condition: Int => Boolean): ConditionalEvent =
      ConditionalEventImpl(scenario, condition)

    class ConditionalEventImpl(scenario: Scenario, condition: Int => Boolean) extends ConditionalEvent:
      override def run(playerId: Int): Option[Event] =
//        if actionIndex < 0 then throw new IllegalArgumentException("The action index can not be a negative number")
        scenario.eventStrategy(playerId)
        scenario.nextEvent

      override def eventStory: EventStory = scenario.eventStory

      override def hasToRun(playerId: Int): Boolean = condition(playerId)

    class EventImpl(scenario: Scenario) extends Event:
      override def run(playerId: Int): Option[Event] =
        scenario.eventStrategy(playerId)
        scenario.nextEvent

      override def eventStory: EventStory = scenario.eventStory
