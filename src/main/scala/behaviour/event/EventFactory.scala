package behaviour.event

import behaviour.event.*

object EventFactory:
  import EventModule.*
  import EventStoryModule.*
  import Event.*
  def InfoEvent(story: EventStory, condition: EventPrecondition): Event = Event(Scenario(story), condition)
