package behaviour
import behaviour.BehaviourModule.StoryGroup
import event.EventModule.*

object BehaviourModule:
  type EventGroup = Seq[ConditionalEvent]
  def EventGroup(elems: ConditionalEvent*): EventGroup = elems
  type StoryGroup = Seq[EventStory]

  /** A choose of an event of the behaviour. It is a tuple2: (eventGroupIndex, eventIndex)
    */
  type Choice = (Int, Int)

  trait Behaviour:
    def getInitialEvents(playerId: Int): Seq[EventGroup]

  object Behaviour:
    def apply(initialEvents: Seq[EventGroup]): Behaviour = BehaviourImpl(initialEvents)

    case class BehaviourImpl(private val initialEvents: Seq[EventGroup]) extends Behaviour:
      override def getInitialEvents(playerId: Int): Seq[EventGroup] =
        initialEvents.map(_.filter(_.hasToRun(playerId))).filter(_.nonEmpty)

    def getStories(events: Seq[EventGroup]): Seq[StoryGroup] =
      events.map(eventGroup => eventGroup.map(m => m.eventStory))

    def printStories(stories: Seq[StoryGroup]): String =
      var result: String = ""
      stories.foreach(storyGroup =>
        result += "Group\n";
        storyGroup.foreach(story =>
          result += s"\t ${story.description}. Available actions:\n\t\t";
          result += story.actions.mkString("\n\t\t")
          result +=
            "\n"
        )
      )
      result

    /** Allow to choose an available event of this behaviour
      *
      * @param choice
      *   see [[Choice]]
      */
    def chooseEvent(currentEvents: Seq[EventGroup])(playerId: Int, choice: (Int, Int)): Seq[EventGroup] =
      try
        val nextOpEvent = currentEvents(choice._1)(choice._2).run(playerId)

        // remove chose EventGroup
        var newEvents = currentEvents.patch(choice._1, Nil, 1)

        // insert next EventGroup
        if nextOpEvent.nonEmpty && nextOpEvent.get.isInstanceOf[ConditionalEvent] then
          val nextCondEvent = nextOpEvent.get.asInstanceOf[ConditionalEvent]
          if nextCondEvent.hasToRun(playerId) then newEvents = newEvents :+ Seq(nextCondEvent)
        newEvents
      catch
        case _: IndexOutOfBoundsException =>
          throw IllegalArgumentException("Chose indexes point to a not existing event. -> " + choice)
