package behaviour

import behaviour.BehaviourModule.StoryGroup
import behaviour.event.EventGroup

/** Allows to convert a sequence of [[EventGroup]] to sequence of [[StoryGroup]] and then to a string
  */
trait StoryHelper:
  trait Converter[T]:
    def stories(seq: Seq[T], playerId: Int): Seq[StoryGroup]

  given Converter[StoryGroup] with
    override def stories(seq: Seq[StoryGroup], playerId: Int): Seq[StoryGroup] = seq

  given Converter[EventGroup] with
    override def stories(seq: Seq[EventGroup], playerId: Int): Seq[StoryGroup] =
      seq.map(eventGroup => eventGroup.map(m => m.eventStory(playerId)))

  def getStories(events: Seq[EventGroup], playerId: Int): Seq[StoryGroup] =
    summon[Converter[EventGroup]].stories(events, playerId)

  def printStories[T: Converter](stories: Seq[T], playerId: Int): String =
    var result: String = ""
    summon[Converter[T]]
      .stories(stories, playerId)
      .foreach(storyGroup =>
        result += "Group\n";
        storyGroup.foreach(story =>
          result += s"\t ${story.description}. Available actions:\n\t\t";
          result += story.choices.mkString("\n\t\t")
          result +=
            "\n"
        )
      )
    result
