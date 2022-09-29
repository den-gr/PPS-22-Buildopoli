package buildopoli.behaviour.event

import java.awt.Choice
import scala.annotation.targetName
import buildopoli.behaviour.event.story.EventStoryModule.*
import buildopoli.behaviour.event.story.EventStoryModule.EventStory
import buildopoli.behaviour.event.story.InteractiveEventStoryModule.InteractiveEventStory

/** Basic elements of game events
  */
object EventModule:
  /** Main logic of event execution. Take in input a player id
    */
  type EventStrategy = Int => Unit

  /** Precondition of event appearance to the player. Take in input a player id
    */
  type EventPrecondition = Int => Boolean

  /** Generate an event story to the specific player. Take in input a player id
    */
  type StoryGenerator = Int => EventStory

  /** A set of elements that are useful for building a custom window of interaction with a player. Can be chained with
    * an successor [[EventGroup]]
    */
  trait Event:
    /** @return
      *   successor event group
      */
    def nextEvents: Option[EventGroup]

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
    def replaceNextEventGroup(nextEv: Option[EventGroup]): Event

  object Event:
    private val WITHOUT_PRECONDITION: EventPrecondition = _ => true
    private val WITHOUT_STRATEGY: EventStrategy = _ => ()

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
    given StoryGeneratorAdapter[InteractiveEventStory] with
      override def adapt(st: InteractiveEventStory): StoryGenerator = _ => st

    /** Flexible builder of Event
      * @param story
      *   event story of new event
      * @param eventStrategy
      *   main strategy of new event. Di default the strategy do nothing
      * @param precondition
      *   precondition for appearance of new event. Di default always appears
      * @param nextEvents
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
        nextEvents: Option[EventGroup] = None
    ): Event = EventImpl(summon[StoryGeneratorAdapter[T]].adapt(story), eventStrategy, precondition, nextEvents)

    private case class EventImpl(
        storyGenerator: StoryGenerator,
        eventStrategy: EventStrategy,
        precondition: EventPrecondition,
        nextEvents: Option[EventGroup]
    ) extends Event:

      override def run(playerId: Int): Unit = eventStrategy(playerId)

      override def eventStory(playerId: Int): EventStory = storyGenerator(playerId)

      override def hasToRun(playerId: Int): Boolean = precondition(playerId)

      override def replaceNextEventGroup(newNextEventGroup: Option[EventGroup]): Event =
        this.copy(nextEvents = newNextEventGroup)

  /** Additional operations for [[Event]]
    */
  object EventOperation:
    extension [T <: Event](e: T)
      @targetName("append next events")
      /** Append to an Event as its nextEvent an another event. Both events must be of the same type
        * @param nextEvents
        *   event that will be appended
        * @return
        *   new event with a new event successor
        */
      def ++(nextEvents: T): T =
        if e.getClass != nextEvents.getClass then
          throw new IllegalArgumentException("Both event must be of the same type")
        e.replaceNextEventGroup(Some(EventGroup(nextEvents))).asInstanceOf[T]
