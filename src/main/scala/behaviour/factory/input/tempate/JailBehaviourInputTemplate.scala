package behaviour.factory.input.tempate

import behaviour.event.EventFactory.EventLogMsg
import behaviour.event.EventStoryModule
import behaviour.event.EventStoryModule.EventStory
import behaviour.factory.input.JailBehaviourInput

object JailBehaviourInputTemplate extends JailBehaviourInput:

  override val blockingTurns: Int = 2

  override val imprisonStory: EventStoryModule.EventStory =
    EventStory(s"You are imprisoned for $blockingTurns turns", Seq("Wait for liberation"))

  override val escapeStory: EventStoryModule.EventStory =
    EventStory(s"You have an opportunity to escape if you get a double with dices", Seq("Try to escape"))

  override val escapeSuccessMsg: EventLogMsg = playerId => s"Player $playerId is escaped from the jail"

  override val escapeFailMsg: EventLogMsg = playerId => s"Player $playerId fails to escape from the jail"
