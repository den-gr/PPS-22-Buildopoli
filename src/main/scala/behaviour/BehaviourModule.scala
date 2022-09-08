package behaviour
import behaviour.BehaviourModule.StoryGroup
import behaviour.event.EventModule
import behaviour.event.EventModule.*
import behaviour.event.EventGroup
import behaviour.event.EventStoryModule.EventStory

import scala.annotation.targetName

object BehaviourModule extends StoryHelper:
  /** Sequence of [[EventStory]]
    */
  type StoryGroup = Seq[EventStory]

  /** A choose of an event of the behaviour. It is a tuple2: (eventGroupIndex, eventIndex)
    */
  type Index = (Int, Int)

  /** Behaviour encapsulate a sequence of game events that can be used/visualized by a player. For every new interaction
    * (a new player turn) Behaviour supply a fresh [[BehaviourIterator]]
    */
  trait Behaviour:
    /** Supply behaviour iterator that is needed to choose events and to see only available event to the specific player
      * @param playerId
      *   id of the player that will be interact with the behaviour
      * @return
      *   a fresh behaviour iterator
      */
    def getBehaviourIterator(playerId: Int): BehaviourIterator

  object Behaviour:
    /** Constructor a [[Behaviour]] based on a sequence of event groups
      * @param initialEvents
      *   event groups of the behaviour
      * @return
      *   a new Behaviour
      */
    def apply(initialEvents: Seq[EventGroup]): Behaviour = BehaviourImpl(initialEvents)

    /** Construct a [[Behaviour]] with only one event group
      * @param singleEventGroup
      *   single event group of Behaviour
      * @return
      *   a new Behaviour
      */
    def apply(singleEventGroup: EventGroup): Behaviour = apply(Seq(singleEventGroup))
    
    @targetName("Constructor with events of a simple single event group")
    /**
     * Construct a [[Behaviour]] with only one event group, take in input events of this event group
     * @param eventsOfSingleEventGroup events
     * @return a new Behaviour
     */
    def apply(eventsOfSingleEventGroup: Event*): Behaviour = apply(EventGroup(eventsOfSingleEventGroup))

    private case class BehaviourImpl(private val initialEvents: Seq[EventGroup]) extends Behaviour:
      override def getBehaviourIterator(playerId: Int): BehaviourIterator =
        BehaviourIterator(getInitialEvents(playerId), playerId)

      private def getInitialEvents(playerId: Int): Seq[EventGroup] =
        initialEvents
          .map(gr => gr.replaceEvents(gr.filter(_.hasToRun(playerId))))
          .filter(_.nonEmpty)
