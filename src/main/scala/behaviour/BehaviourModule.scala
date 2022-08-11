package behaviour

import events.EventModule.*

object BehaviourModule extends App:
  type EventGroup = Seq[Event]
  type StoryGroup = Seq[EventStory]

  /** A choose of a event of the behaviour. It is a tuple2: (eventGroupIndex, eventIndex)
    */
  type Choose = (Int, Int)

  trait Behaviour:
    def currentStories: Seq[StoryGroup]

    /** Allow to choose an available event of this behaviour
      * @param choose
      *   see [[Choose]]
      */
    def chooseEvent(playerId: Int, choose: Choose): Unit

  object Behaviour:
    def apply(initialEvents: Seq[EventGroup]): Behaviour = BehaviourImpl(initialEvents)

    case class BehaviourImpl(initialEvents: Seq[EventGroup]) extends Behaviour:
      var currentEvents: Seq[EventGroup] = Seq()
      override def currentStories: Seq[StoryGroup] =
        currentEvents = initialEvents;
        println("Run Behaviour")
        println(s"> ${initialEvents.head.head.eventStory.description}")
        println(s">index  ${initialEvents.indexOf(initialEvents.head)}")
        initialEvents.map(eventGroup => eventGroup.map(m => m.eventStory))

      override def chooseEvent(playerId: Int, choose: Choose): Unit =
        try
          val nextOpEvent = currentEvents(choose._1)(choose._2).run(playerId)
          currentEvents = currentEvents.patch(choose._1, Nil, 1)
//          if nextOpEvent.nonEmpty then nextOpEvent.get
        catch case e: IndexOutOfBoundsException => throw IllegalArgumentException("Player chose not existing event")
