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

    case class BehaviourImpl(private val initialEvents: Seq[EventGroup]) extends Behaviour :
      override def getInitialEvents(playerId: Int): Seq[EventGroup] =
        initialEvents.map(_.filter(_.hasToRun(playerId))).filter(_.nonEmpty)
    
    trait StoryConverter[T]:
      def stories(seq : Seq[T]): Seq[StoryGroup]
    
    given StoryConverter[StoryGroup] with 
      override  def stories(seq : Seq[StoryGroup]) : Seq[StoryGroup] = seq

    given StoryConverter[EventGroup] with
      override def stories(seq: Seq[EventGroup]): Seq[StoryGroup] = getStories(seq)

    def getStories(events: Seq[EventGroup]): Seq[StoryGroup] =
      events.map(eventGroup => eventGroup.map(m => m.eventStory))

    
    def printStories[T: StoryConverter](stories: Seq[T]): String =
      var result: String = ""
      summon[StoryConverter[T]]
        .stories(stories)
        .foreach(storyGroup =>
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
        val ev = currentEvents(choice._1)(choice._2)
        ev.run(playerId)
        val nextOpEvent = ev.nextEvent

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
