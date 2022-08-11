package behaviour

import events.EventModule.*

object BehaviourModule extends App:
  type EventGroup = Seq[Event]
  type StoryGroup = Seq[EventStory]
  type Choose = (Int, Int) // (groupIndex, eventIndex)

  trait Behaviour:
    def currentStories: Seq[StoryGroup]
    def chooseEvent(choose: Choose): Unit

  object Behaviour:
    def apply(initialEvents: Seq[EventGroup]): Behaviour = BehaviourImpl(initialEvents)

    case class BehaviourImpl(initialEvents: Seq[EventGroup]) extends Behaviour:
//      var currentEvents = Seq[EventGroup]
      override def currentStories: Seq[StoryGroup] =
        println("Run Behaviour")
        println(s"> ${initialEvents.head.head.eventStory.description}")
        println(s">index  ${initialEvents.indexOf(initialEvents.head)}")
//        val stories = List()
        initialEvents.map(eventGroup => eventGroup.map(m => m.eventStory))

      override def chooseEvent(choose: Choose): Unit = ???
