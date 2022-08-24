package behaviour
import behaviour.BehaviourModule.StoryGroup
import behaviour.event.EventModule
import behaviour.event.EventModule.*
import behaviour.event.EventStoryModule.EventStory

object BehaviourModule:
  
  type StoryGroup = Seq[EventStory]

  /** A choose of an event of the behaviour. It is a tuple2: (eventGroupIndex, eventIndex)
    */
  type Index = (Int, Int)

  trait Behaviour:
    def getInitialEvents(playerId: Int): Seq[EventGroup]

  object Behaviour:
    def apply(initialEvents: Seq[EventGroup]): Behaviour = BehaviourImpl(initialEvents)

    case class BehaviourImpl(private val initialEvents: Seq[EventGroup]) extends Behaviour:
      override def getInitialEvents(playerId: Int): Seq[EventGroup] =
        initialEvents
          .map(gr => EventGroup(gr.filter(_.hasToRun(playerId)), gr.isAtomic))
          .filter(_.nonEmpty)

    trait StoryConverter[T]:
      def stories(seq: Seq[T], playerId: Int): Seq[StoryGroup]

    given StoryConverter[StoryGroup] with
      override def stories(seq: Seq[StoryGroup], playerId: Int): Seq[StoryGroup] = seq

    given StoryConverter[EventGroup] with
      override def stories(seq: Seq[EventGroup], playerId: Int): Seq[StoryGroup] =
        seq.map(eventGroup => eventGroup.seq.map(m => m.eventStory(playerId)))

    def getStories(events: Seq[EventGroup], playerId: Int): Seq[StoryGroup] =
      summon[StoryConverter[EventGroup]].stories(events, playerId)

    def printStories[T: StoryConverter](stories: Seq[T], playerId: Int): String =
      var result: String = ""
      summon[StoryConverter[T]]
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

    /** Allow to choose an available event of this behaviour
      *
      * @param index
      *   see [[Index]]
      */
    def chooseEvent(currentEvents: Seq[EventGroup])(playerId: Int, index: (Int, Int)): Seq[EventGroup] =
      try
        val event = currentEvents(index._1)(index._2)
        event.run(playerId)
        val nextOpEvents: Option[EventGroup] = event.nextEvent

        // remove chose EventGroup
        var newEvents = currentEvents.patch(index._1, Nil, 1)

        // insert next EventGroup
        if nextOpEvents.nonEmpty  then
          val nextEventGroup : EventGroup = EventGroup(
            for 
              ev <- nextOpEvents.get
              if ev.hasToRun(playerId)
            yield ev
          , nextOpEvents.get.isAtomic)
          newEvents = newEvents :+ nextEventGroup
        newEvents
      catch
        case _: IndexOutOfBoundsException =>
          throw IllegalArgumentException("Chose indexes point to a not existing event. -> " + index)
