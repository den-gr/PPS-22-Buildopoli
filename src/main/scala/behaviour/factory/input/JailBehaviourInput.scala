package behaviour.factory.input

import behaviour.event.EventFactory.EventLogMsg
import behaviour.event.EventStoryModule.EventStory

/** A simple template with default values of the input to [[StandardBehaviourFactory]] the allow to crete JailBehaviour
  */
trait JailBehaviourInput:
  /** The number of turns that a player will be blocked
    */
  val blockingTime: Int = 2

  /** [[EventStory]] That inform a player about it imprisoning
    */
  val imprisonStory: EventStory =
    EventStory(s"You are imprisoned for $blockingTime turns", Seq("Wait for liberation"))

  /** [[EventStory]] That inform a player about of an opportunity to escape from prison
    */
  val escapeStory: EventStory =
    EventStory(s"You have an opportunity to escape if you get a double with dices", Seq("Try to escape"))

  /** A lambda that take in input a player id end return msg about successful escape
    */
  val escapeSuccessMsg: EventLogMsg = playerId => s"Player $playerId is escaped from the jail"

  /** A lambda that take in input a player id end return msg about failed escape
    */
  val escapeFailMsg: EventLogMsg = playerId => s"Player $playerId fails to escape from the jail"
