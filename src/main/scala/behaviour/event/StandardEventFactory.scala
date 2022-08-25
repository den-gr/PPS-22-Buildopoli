package behaviour.event

import behaviour.event.EventModule.EventPrecondition
import behaviour.event.EventStoryModule.EventStory
import EventModule.*
import behaviour.event.EventFactory.EventLogMsg

trait StandardEventFactory:
  def ImprisonEvent(story: EventStory, blockingTime: Int): Event
  def EscapeEvent(story: EventStory,escapeSuccessMsg: EventLogMsg, escapeFailMsg: EventLogMsg): Event