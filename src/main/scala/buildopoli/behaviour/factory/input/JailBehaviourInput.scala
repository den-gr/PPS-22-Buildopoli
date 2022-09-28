package buildopoli.behaviour.factory.input

import buildopoli.behaviour.BehaviourModule
import buildopoli.behaviour.factory.EventFactory.EventLogMsg
import buildopoli.behaviour.event.story.EventStoryModule
import buildopoli.behaviour.event.story.EventStoryModule.EventStory

/** A simple template with default values of the input to [[StandardBehaviourFactory]] that allows to crete a jail
  * [[BehaviourModule.Behaviour]] object
  */
trait JailBehaviourInput:
  /** The number of turns that a player will be blocked
    */
  val blockingTurns: Int

  /** [[EventStory]] That inform a player about it imprisoning
    */
  val imprisonStory: EventStory

  /** [[EventStory]] That inform a player about of an opportunity to escape from prison
    */
  val escapeStory: EventStory

  /** A lambda that take in input a player id end return msg about successful escape
    */
  val escapeSuccessMsg: EventLogMsg

  /** A lambda that take in input a player id end return msg about failed escape
    */
  val escapeFailMsg: EventLogMsg

class JailBehaviourDefaultInput(
    override val blockingTurns: Int = 2,
    override val imprisonStory: EventStoryModule.EventStory =
      EventStory(s"You are imprisoned for 2 turns", "Wait for liberation"),
    override val escapeStory: EventStoryModule.EventStory =
      EventStory(s"You have an opportunity to escape if you get a double with dices", "Try to escape"),
    override val escapeSuccessMsg: EventLogMsg = playerId => s"Player $playerId is escaped from the jail",
    override val escapeFailMsg: EventLogMsg = playerId => s"Player $playerId fails to escape from the jail"
) extends JailBehaviourInput
