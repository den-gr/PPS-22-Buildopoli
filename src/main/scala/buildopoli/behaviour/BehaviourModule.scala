package buildopoli.behaviour

import buildopoli.behaviour.event.EventModule.*
import buildopoli.behaviour.event.story.EventStoryModule.EventStory
import buildopoli.behaviour.event.{EventGroup, EventModule}
import buildopoli.behaviour.event.EventModule.Event
import buildopoli.behaviour.event.story.EventStoryModule.EventStory

import scala.annotation.targetName
import scala.collection.immutable.Seq

object BehaviourModule:

  /** Behaviour encapsulate the game events that can be used/visualized by a player. For every new player turn Behaviour
    * supply a fresh [[BehaviourExplorer]]
    */
  trait Behaviour:
    /** Supply behaviour explorer that is needed to choose events and to see only available event to the specific player
      * @param playerId
      *   id of the player that will be interact with the behaviour
      * @return
      *   a fresh behaviour explorer
      */
    def getBehaviourExplorer(playerId: Int): BehaviourExplorer

    /** Add event groups to the behaviour
      * @param events
      *   events that must be added to behaviour
      * @return
      *   new behaviour with current and new events
      */
    def addEventGroups(events: Seq[EventGroup]): Behaviour

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

    @targetName("Construct a Behaviour with a  single event group")
    /** Construct a [[Behaviour]] with only one event group, take in input events of this event group
      * @param eventsOfSingleEventGroup
      *   events
      * @return
      *   a new Behaviour
      */
    def apply(eventsOfSingleEventGroup: Event*): Behaviour = apply(EventGroup(eventsOfSingleEventGroup))

    private case class BehaviourImpl(initialEvents: Seq[EventGroup]) extends Behaviour:

      override def getBehaviourExplorer(playerId: Int): BehaviourExplorer =
        BehaviourExplorer(getInitialEvents(playerId), playerId)

      override def addEventGroups(events: Seq[EventGroup]): Behaviour = Behaviour(initialEvents :++ events)

      private def getInitialEvents(playerId: Int): Seq[EventGroup] =
        initialEvents
          .map(gr => gr.replaceEvents(gr.filter(_.hasToRun(playerId))))
          .filter(_.nonEmpty)

    /** Combine [[BehaviourExplorer]] of two behaviour
      * @param b1
      *   first behaviour
      * @param b2
      *   second behaviour
      * @param playerId
      *   player for which the behaviour explorer will be created
      * @return
      *   a combination of two behaviour explorers
      */
    def combineExplorers(b1: Behaviour, b2: Behaviour, playerId: Int): BehaviourExplorer =
      Behaviour(
        b1.getBehaviourExplorer(playerId).currentEvents ++
          b2.getBehaviourExplorer(playerId).currentEvents
      ).getBehaviourExplorer(playerId)
