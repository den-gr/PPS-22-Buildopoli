package behaviour.event

import java.awt.Choice
import scala.annotation.targetName
import behaviour.event.EventStoryModule
import behaviour.event.EventStoryModule.*

object EventModule:
  type EventStrategy = Int => Unit
  type EventPrecondition = Int => Boolean
  type StoryGenerator = Int => EventStory

  trait EventGroup extends Seq[Event]:
    val events: Seq[Event]
    export events.*
    val isAtomic: Boolean
    // TODO def isMandatory: Boolean

  object EventGroup:
    def apply(elems: Event*): EventGroup = EventGroupImpl(elems)
    def apply(elems: Seq[Event], isAtomic: Boolean): EventGroup = EventGroupImpl(elems, isAtomic)

    class EventGroupImpl(override val events: Seq[Event], override val isAtomic: Boolean = false) extends EventGroup

  trait Event:
    def nextEvent: Option[EventGroup]
    def run(playerId: Int): Unit
    def eventStory(playerId: Int): EventStory
    def copy(nextEv: Option[EventGroup]): Event
    def hasToRun(playerId: Int): Boolean

  object Event:
    val WITHOUT_PRECONDITION: EventPrecondition = _ => true
    val WITHOUT_STRATEGY: EventStrategy = _ => ()

    trait StoryGeneratorAdapter[T]:
      def adapt(st: T): StoryGenerator
    given StoryGeneratorAdapter[StoryGenerator] with
      override def adapt(st: StoryGenerator): StoryGenerator = st
    given StoryGeneratorAdapter[EventStory] with
      override def adapt(st: EventStory): StoryGenerator = _ => st

    def apply[T: StoryGeneratorAdapter](
        story: T,
        eventStrategy: EventStrategy = WITHOUT_STRATEGY,
        precondition: EventPrecondition = WITHOUT_PRECONDITION,
        nextEvent: Option[EventGroup] = None
    ): Event = EventImpl(summon[StoryGeneratorAdapter[T]].adapt(story), eventStrategy, precondition, nextEvent)

    case class EventImpl(
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
