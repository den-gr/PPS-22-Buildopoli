package behaviour.event

import behaviour.event.EventModule.EventPrecondition
import behaviour.event.EventStoryModule.{EventStory, InteractiveEventStory}
import EventModule.*
import behaviour.event.EventFactory.EventLogMsg

/** Allows easily to create basic events
  */
trait BasicEventFactory:

  /** @param story
    *   event description
    * @param amount
    *   of money that will be withdraw from player
    * @return
    *   event that withdraw money of player
    */
  def WithdrawMoneyEvent(story: EventStory, amount: Int): Event

  /** @param story
    *   event description
    * @param blockingTime
    *   the number of turns that player will be blocked
    * @return
    *   event that imprison a player
    */
  def ImprisonEvent(story: EventStory, blockingTime: Int): Event

  /** @param story
    *   event description
    * @param escapeSuccessMsg
    *   a message visualized to player when the escape was successful
    * @param escapeFailMsg
    *   a message visualized to player when the escape failed
    * @return
    *   event that allow to player to escape from prison if he get a double with two dices
    */
  def EscapeEvent(story: EventStory, escapeSuccessMsg: EventLogMsg, escapeFailMsg: EventLogMsg): Event

  def BuyTerrainEvent(story: EventStory): Event

  def GetRentEvent(story: EventStory, notMoneyErrMsg: String): Event
