package behaviour.event

import behaviour.event.EventModule.EventPrecondition
import behaviour.event.EventStoryModule.EventStory
import EventModule.*

trait StandardEventFactory:
  def ImprisonEvent(blockingTime: Int): Event
  def EscapeEvent(): Event 
