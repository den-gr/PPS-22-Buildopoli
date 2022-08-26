package behaviour.event

object EventStoryModule:
  enum Result:
    case ERR(msg: String)
    case OK
  type Interaction = () => Result

  trait EventStory:
    def description: String

    def choices: Seq[String]

    def isSingleAction: Boolean = choices.length == 1

  trait StoryInteraction:
    evSt: EventStory =>
    def interactions: Seq[Interaction]

    def choicesAndInteractions: Seq[(String, Interaction)] =
      if choices.length != interactions.length then
        throw IllegalStateException("Each description must have a corresponding action")
      for i <- choices.indices
      yield (choices(i), interactions(i))

  trait InteractiveEventStory extends EventStory with StoryInteraction

  object EventStory:
    val MAIN_ACTION = 0

    def apply(desc: String, choices: Seq[String]): EventStory = EventStoryImpl(desc, choices)

    def apply(desc: String, choices: Seq[String], interactions: Seq[Interaction]): InteractiveEventStory =
      EventStoryActionsImpl(desc, choices, interactions)

    class EventStoryImpl(
        override val description: String,
        override val choices: Seq[String]
    ) extends EventStory:
      override def toString: String =
        s"$description \n\t" + choices.mkString("\n\t")

    case class EventStoryActionsImpl(
        description: String,
        choices: Seq[String],
        interactions: Seq[Interaction]
    ) extends InteractiveEventStory:
      if interactions.length != choices.length then
        throw new IllegalArgumentException("The quantity of choices and events must be the same")
