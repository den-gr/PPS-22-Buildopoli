package lib.behaviour.event.story

import lib.behaviour.event.story.InteractiveEventStoryModule.*

/** Define interactive and not interactive event stories
  */
object EventStoryModule:
  /** Sequence of [[EventStory]]
    */
  type StoryGroup = Seq[EventStory]

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

    /** [[InteractiveEventStory]] constructor
      * @param desc
      *   event story description
      * @param choicesAndInteractions
      *   tuples with texts of possible player choices and corresponding interaction for each choice
      * @return
      *   an interactive event story
      */
    def apply(desc: String, choicesAndInteractions: Seq[(String, Interaction)]): InteractiveEventStory =
      apply(desc, choicesAndInteractions.map(_._1), choicesAndInteractions.map(_._2))

    /** Transform [[EventStory]] in [[InteractiveEventStory]] by adding [[Interaction]]
      * @param story
      *   base event story
      * @param interactions
      *   that will be added to the event story
      * @return
      *   an interactive event story
      */
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
