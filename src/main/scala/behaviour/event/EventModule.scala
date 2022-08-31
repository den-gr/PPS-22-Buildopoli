package behaviour.event

import java.awt.Choice
import scala.annotation.targetName
import behaviour.event.EventStoryModule
import behaviour.event.EventStoryModule.*

object EventModule:
  /** Main logic of event action. Take in input a player id
    */
  type EventStrategy = Int => Unit

  /** Precondition of event appearance to a player. Take in input a player id
    */
  type EventPrecondition = Int => Boolean

  /** Generate event story for a player. Take in input a player id
    */
  type StoryGenerator = Int => EventStory

  /** A set of elements that are useful for building a custom window of interaction with a player. Can be chained with
    * an successor [[EventGroup]]
    */
  trait Event:
    /** @return
      *   successor event group
      */
    def nextEvent: Option[EventGroup]

    /** Execute event strategy
      * @param playerId
      *   id of a player
      */
    def run(playerId: Int): Unit

    /** @param playerId
      *   id of a player
      * @return
      *   event story of this event
      */
    def eventStory(playerId: Int): EventStory

    /** Verify that event precondition is true for a player
      * @param playerId
      *   id of a player
      * @return
      *   true if event can be visible to player
      */
    def hasToRun(playerId: Int): Boolean

    /** Copy this event and change the event chain successor
      * @param nextEv
      *   successor event group
      * @return
      *   copy of this event
      */
    def copy(nextEv: Option[EventGroup]): Event

  object Event:
    val WITHOUT_PRECONDITION: EventPrecondition = _ => true
    val WITHOUT_STRATEGY: EventStrategy = _ => ()

    /** Allows to adapt EventStory as StoryGenerator
      * @tparam T
      *   object that can be adapt into StoryGenerator
      */
    trait StoryGeneratorAdapter[T]:
      def adapt(st: T): StoryGenerator

    given StoryGeneratorAdapter[StoryGenerator] with
      override def adapt(st: StoryGenerator): StoryGenerator = st
    given StoryGeneratorAdapter[EventStory] with
      override def adapt(st: EventStory): StoryGenerator = _ => st

    /** Flexible builder of Event
      * @param story
      *   event story of new event
      * @param eventStrategy
      *   main strategy of new event. Di default the strategy do nothing
      * @param precondition
      *   precondition for appearance of new event. Di default always appears
      * @param nextEvent
      *   successor event group. Di default is empty
      * @tparam T
      *   can be an [[EventStory]] or a [[StoryGenerator]]
      * @return
      *   a new built event
      */
    def apply[T: StoryGeneratorAdapter](
        story: T,
        eventStrategy: EventStrategy = WITHOUT_STRATEGY,
        precondition: EventPrecondition = WITHOUT_PRECONDITION,
        nextEvent: Option[EventGroup] = None
    ): Event = EventImpl(summon[StoryGeneratorAdapter[T]].adapt(story), eventStrategy, precondition, nextEvent)

    private case class EventImpl(
        storyGenerator: StoryGenerator,
        eventStrategy: EventStrategy,
        precondition: EventPrecondition,
        nextEvent: Option[EventGroup]
    ) extends Event:

      override def run(playerId: Int): Unit = eventStrategy(playerId)

      override def eventStory(playerId: Int): EventStory = storyGenerator(playerId)

      override def hasToRun(playerId: Int): Boolean = precondition(playerId)

      override def copy(newNextEvent: Option[EventGroup]): Event = this.copy(nextEvent = newNextEvent)

  object EventOperation:
    extension [T <: Event](e: T)
      @targetName("append")
      def ++(nextEvent: T): T =
        if e.getClass != nextEvent.getClass then
          throw new IllegalArgumentException("Both event must be of the same type")
        e.copy(Some(EventGroup(nextEvent))).asInstanceOf[T]
