package behaviour.event

/** Define interactive and not interactive event stories
  */
object EventStoryModule:
  /** Description and possible action of an event
    */
  trait EventStory:
    /** Explain to players what happens in this event
      * @return
      *   textual event description
      */
    def description: String

    /** Allows interact with event, mostly there are only one choice
      * @return
      *   textual reactions of player to event
      */
    def choices: Seq[String]

  /** Result of [[Interaction]]
    */
  enum Result:
    case ERR(msg: String)
    case OK

  /** Event interaction allows to player react to event with some action. Take in input player id
    */
  type Interaction = Int => Result

  /** [[EventStory]] extension that for each choice define an [[Interaction]]
    */
  trait StoryInteraction:
    evSt: EventStory =>

    /** @return
      *   list of story interaction
      */
    def interactions: Seq[Interaction]

    /** Derivable method that group story choices with interaction
      * @return
      *   grouped story choices with interaction
      */
    def choicesAndInteractions: Seq[(String, Interaction)] =
      for i <- choices.indices
      yield (choices(i), interactions(i))

  /** Event story with story interactions
    */
  trait InteractiveEventStory extends EventStory with StoryInteraction

  object EventStory:
    /** [[EventStory]]constructor
      * @param desc
      *   event story description
      * @param singleChoice
      *   text of single choice of this event
      * @return
      *   an event story
      */
    def apply(desc: String, singleChoice: String): EventStory = EventStoryImpl(desc, Seq(singleChoice))

    /** [[InteractiveEventStory]] constructor
      * @param desc
      *   event story description
      * @param choices
      *   texts of possible player choices
      * @param interactions
      *   corresponding interaction for each choice
      * @return
      *   an interactive event story
      * @throws IllegalArgumentException
      *   if length of choices is different of length of interactions
      */
    def apply(desc: String, choices: Seq[String], interactions: Seq[Interaction]): InteractiveEventStory =
      if choices.length != interactions.length then
        throw IllegalArgumentException("Each description must have a corresponding action")
      EventStoryInteractionsImpl(desc, choices, interactions)

    def apply(story: EventStory, interactions: Seq[Interaction]): InteractiveEventStory =
      apply(story.description, story.choices, interactions)
      

    private class EventStoryImpl(
        override val description: String,
        override val choices: Seq[String]
    ) extends EventStory:

      override def toString: String =
        s"$description \n\t" + choices.mkString("\n\t")

    private class EventStoryInteractionsImpl(
        override val description: String,
        override val choices: Seq[String],
        override val interactions: Seq[Interaction]
    ) extends InteractiveEventStory
