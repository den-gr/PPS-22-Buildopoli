package behaviour.event

object EventStoryModule:
  type Action = () => Unit

  trait EventStory:
    def description: String

    def choices: Seq[String]

    def isSingleAction: Boolean = choices.length == 1

  trait StoryAction:
    evSt: EventStory =>
    def actions: Seq[() => Unit]

    def choicesAndActions: Seq[(String, Action)] =
      if choices.length != actions.length then
        throw IllegalStateException("Each description must have a corresponding action")
      for
        choice <- choices
        action <- actions
      yield (choice, action)

  trait EventStoryActions extends EventStory with StoryAction

  object EventStory:
    val MAIN_ACTION = 0

    def apply(desc: String, choices: Seq[String]): EventStory = EventStoryImpl(desc, choices)

    def apply(desc: String, choices: Seq[String], actions: Seq[Action]): EventStoryActions =
      EventStoryActionsImpl(desc, choices, actions)

    class EventStoryImpl(
        override val description: String,
        override val choices: Seq[String]
    ) extends EventStory:
      override def toString: String =
        s"$description \n\t" + choices.mkString("\n\t")

    case class EventStoryActionsImpl(
        description: String,
        choices: Seq[String],
        actions: Seq[Action]
    ) extends EventStoryActions
        with StoryAction
