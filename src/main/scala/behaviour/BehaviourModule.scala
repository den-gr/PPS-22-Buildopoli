package behaviour

import events.EventModule.*

object BehaviourModule extends App:
  type EventGroup = Seq[Event]
  type StoryGroup = Seq[EventStory]

  /** A choose of an event of the behaviour. It is a tuple2: (eventGroupIndex, eventIndex)
    */
  type Choice = (Int, Int)

  trait Behaviour:
    def startBehaviour(): Seq[StoryGroup]

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
      override def startBehaviour(): Seq[StoryGroup] =
        currentEvents = initialEvents
        getStories

      override def chooseEvent(playerId: Int, choice: Choice): Seq[StoryGroup] =
        try
          val nextOpEvent = currentEvents(choice._1)(choice._2).run(playerId)
          println(s"Before $currentEvents")

          // remove chose EventGroup
          currentEvents = currentEvents.patch(choice._1, Nil, 1)
          println(s"After $currentEvents")

          // insert next EventGroup
          if nextOpEvent.nonEmpty then currentEvents = currentEvents :+ Seq(nextOpEvent.get)
        catch case _: IndexOutOfBoundsException => throw IllegalArgumentException("Player chose not existing event")
        getStories

      private def getStories: Seq[StoryGroup] =
        currentEvents.map(eventGroup => eventGroup.map(m => m.eventStory))
