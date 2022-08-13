package behaviour

import events.EventModule.*

object BehaviourModule extends App:
  type EventGroup = Seq[Event]
  type StoryGroup = Seq[EventStory]

  /** A choose of a event of the behaviour. It is a tuple2: (eventGroupIndex, eventIndex)
    */
  type Choose = (Int, Int)

  trait Behaviour:
    def startBehaviour: Seq[StoryGroup]

    /** Allow to choose an available event of this behaviour
      * @param choose
      *   see [[Choose]]
      */
    def chooseEvent(playerId: Int, choose: Choose): Seq[StoryGroup]

  object Behaviour:
    def apply(initialEvents: Seq[EventGroup]): Behaviour = BehaviourImpl(initialEvents)

    case class BehaviourImpl(initialEvents: Seq[EventGroup]) extends Behaviour:
      var currentEvents: Seq[EventGroup] = Seq()
      override def startBehaviour: Seq[StoryGroup] =
        currentEvents = initialEvents
        getStories

      override def chooseEvent(playerId: Int, choose: Choose): Seq[StoryGroup] =
        try
          val nextOpEvent = currentEvents(choose._1)(choose._2).run(playerId)
          println(s"Before $currentEvents")

          // remove used EventGroup
          currentEvents = currentEvents.patch(choose._1, Nil, 1)
          println(s"After $currentEvents")

          // insert new event Group
          if nextOpEvent.nonEmpty then currentEvents = currentEvents :+ Seq(nextOpEvent.get)
        catch case _: IndexOutOfBoundsException => throw IllegalArgumentException("Player chose not existing event")
        getStories

      private def getStories: Seq[StoryGroup] =
        currentEvents.map(eventGroup => eventGroup.map(m => m.eventStory))
