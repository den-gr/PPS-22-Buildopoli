package buildopoli.behaviour.event.story

import buildopoli.behaviour.event.story.EventStoryModule.EventStory

/** Module with all elements of interactive event stories
  */
object InteractiveEventStoryModule:
  /** [[EventStory]] extension that for each choice define an [[Interaction]]
    */
  trait StoryInteraction:
    evSt: EventStory =>

    /** @return
      *   list of story interaction
      */
    def interactions: Seq[Interaction]

    /** Derivable method that group story choices with interaction
      *
      * @return
      *   grouped story choices with interaction
      */
    def choicesAndInteractions: Seq[(String, Interaction)] =
      for i <- choices.indices
      yield (choices(i), interactions(i))

  /** Event story with story interactions
    */
  trait InteractiveEventStory extends EventStory with StoryInteraction

  /** Result of [[Interaction]]
    */
  enum Result:
    case ERR(msg: String)
    case OK

  /** Event interaction allows to player react to event with some action. Take in input player id
    */
  type Interaction = Int => Result
