package behaviour

import events.EventModule.*

object BehaviourModule extends App:
  type EventGroup = Seq[ConditionalEvent]
  def EventGroup(elems: ConditionalEvent*) = elems
  type StoryGroup = Seq[EventStory]

  /** A choose of an event of the behaviour. It is a tuple2: (eventGroupIndex, eventIndex)
    */
  type Choice = (Int, Int)

  trait Behaviour:
    def startBehaviour(playerId: Int): Seq[StoryGroup]

    /** Allow to choose an available event of this behaviour
      *
      * @param choice
      *   see [[Choice]]
      */
    def chooseEvent(playerId: Int, choice: Choice): Seq[StoryGroup]

  object Behaviour:
    def apply(initialEvents: Seq[EventGroup]): Behaviour = BehaviourImpl(initialEvents)

    case class BehaviourImpl(initialEvents: Seq[EventGroup]) extends Behaviour:
      var currentEvents: Seq[EventGroup] = Seq()

      override def startBehaviour(playerId: Int): Seq[StoryGroup] =
        currentEvents = initialEvents.map(_.filter(_.hasToRun(playerId))).filter(_.nonEmpty)
        getStories

      override def chooseEvent(playerId: Int, choice: Choice): Seq[StoryGroup] =
        try
          val nextOpEvent = currentEvents(choice._1)(choice._2).run(playerId)

          // remove chose EventGroup
          currentEvents = currentEvents.patch(choice._1, Nil, 1)

          // insert next EventGroup
          if nextOpEvent.nonEmpty && nextOpEvent.get.isInstanceOf[ConditionalEvent] then
            val nextCondEvent = nextOpEvent.get.asInstanceOf[ConditionalEvent]
            if nextCondEvent.hasToRun(playerId) then currentEvents = currentEvents :+ Seq(nextCondEvent)
        catch case _: IndexOutOfBoundsException => throw IllegalArgumentException("Player chose not existing event")
        getStories

      private def getStories: Seq[StoryGroup] =
        currentEvents.map(eventGroup => eventGroup.map(m => m.eventStory))

  object BehaviourPrinter:
    def createPrintableStories(stories: Seq[StoryGroup]): String =
      var result: String = ""
      stories.foreach(storyGroup =>
        result += "Group\n";
        storyGroup.foreach(story =>
          result += s"\t ${story.description}. Available actions:\n\t\t";
          result +=  story.actions.mkString("\n\t\t")
          result += "\n"
        )
      )
      result
