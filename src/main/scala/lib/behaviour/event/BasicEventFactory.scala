package lib.behaviour.event

import EventModule.EventPrecondition
import EventStoryModule.{EventStory, InteractiveEventStory}
import EventModule.*
import EventFactory.EventLogMsg

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
    *   event that allows to player to escape from prison if he get a double with two dices
    */
  def EscapeEvent(story: EventStory, escapeSuccessMsg: EventLogMsg, escapeFailMsg: EventLogMsg): Event

  /** @param story
    *   event description, story choice will be united with a story interaction 
    * @return event that allows to player to buy terrain to became its owner
    */
  def BuyTerrainEvent(story: EventStory): Event

  /**
   * 
   * @param story event description, story choice will be united with a story interaction 
   * @param notMoneyErrMsg If a player has not enough money to pay rent, he will see this msg after click on story choice
   * @return Event that force to pay rent to the terrain owner
   */
  def GetRentEvent(story: EventStory, notMoneyErrMsg: String): Event

  def BuildTokenEvent(storyDescription: String): Event
