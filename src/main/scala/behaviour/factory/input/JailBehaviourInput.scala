package behaviour.factory.input

import behaviour.event.EventFactory.EventLogMsg
import behaviour.event.EventStoryModule.EventStory

/** A simple template with default values of the input to [[StandardBehaviourFactory]] that allows to crete
  * a jail [[behaviour.BehaviourModule.Behaviour]] object
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
