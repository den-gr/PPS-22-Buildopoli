package behaviour
import behaviour.BehaviourModule.StoryGroup
import behaviour.event.EventModule
import behaviour.event.EventModule.*
import behaviour.event.EventGroup
import behaviour.event.EventStoryModule.EventStory

import scala.annotation.targetName

object BehaviourModule extends StoryConverter:

  type StoryGroup = Seq[EventStory]

  /** A choose of an event of the behaviour. It is a tuple2: (eventGroupIndex, eventIndex)
    */
  type Index = (Int, Int)

  trait Behaviour:
    def getBehaviourIterator(playerId: Int): BehaviourIterator

  object Behaviour:
    def apply(initialEvents: Seq[EventGroup]): Behaviour = BehaviourImpl(initialEvents)
    def apply(singleEventGroup: EventGroup): Behaviour = apply(Seq(singleEventGroup))
    @targetName("Constructor with events of a simple single event group")
    def apply(eventsOfSingleEventGroup: Event*): Behaviour = apply(EventGroup(eventsOfSingleEventGroup))

    private case class BehaviourImpl(private val initialEvents: Seq[EventGroup]) extends Behaviour:
      override def getBehaviourIterator(playerId: Int): BehaviourIterator =
        BehaviourIterator(getInitialEvents(playerId), playerId)

      private def getInitialEvents(playerId: Int): Seq[EventGroup] =
        initialEvents
          .map(gr => EventGroup(gr.filter(_.hasToRun(playerId)), gr.isAtomic))
          .filter(_.nonEmpty)
