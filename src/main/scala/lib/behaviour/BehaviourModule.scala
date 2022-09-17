package lib.behaviour

import lib.behaviour.event.EventModule.*
import lib.behaviour.event.story.EventStoryModule.EventStory
import lib.behaviour.event.{EventGroup, EventModule}
import lib.behaviour.event.EventModule.Event
import lib.behaviour.event.story.EventStoryModule.EventStory

import scala.annotation.targetName

object BehaviourModule:

  /** Behaviour encapsulate a sequence of game events that can be used/visualized by a player. For every new interaction
    * (a new player turn) Behaviour supply a fresh [[BehaviourExplorer]]
    */
  trait Behaviour:
    /** Supply behaviour explorer that is needed to choose events and to see only available event to the specific player
      * @param playerId
      *   id of the player that will be interact with the behaviour
      * @return
      *   a fresh behaviour explorer
      */
    def getBehaviourExplorer(playerId: Int): BehaviourExplorer

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
    /** Construct a [[Behaviour]] with only one event group, take in input events of this event group
      * @param eventsOfSingleEventGroup
      *   events
      * @return
      *   a new Behaviour
      */
    def apply(eventsOfSingleEventGroup: Event*): Behaviour = apply(EventGroup(eventsOfSingleEventGroup))

    private case class BehaviourImpl(private val initialEvents: Seq[EventGroup]) extends Behaviour:
      override def getBehaviourExplorer(playerId: Int): BehaviourExplorer =
        BehaviourExplorer(getInitialEvents(playerId), playerId)

      private def getInitialEvents(playerId: Int): Seq[EventGroup] =
        initialEvents
          .map(gr => gr.replaceEvents(gr.filter(_.hasToRun(playerId))))
          .filter(_.nonEmpty)
