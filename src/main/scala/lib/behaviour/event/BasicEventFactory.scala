package lib.behaviour.event

import EventModule.EventPrecondition
import lib.behaviour.event.story.EventStoryModule.EventStory
import EventModule.*
import lib.behaviour.event.EventFactory.EventLogMsg

/** Allows easily to create basic events
  */
trait BasicEventFactory:

  /** @param story
    *   event description
    * @param amount
    *   of money that will be withdraw from player
    * @param resultOfWithdrawingMsg
    *   a message for the game log that will inform how many money remain on player bank account, the number will be
    *   added after the message
    * @return
    *   event that withdraw money of player
    */
  def WithdrawMoneyEvent(
      story: EventStory,
      amount: Int,
      resultOfWithdrawingMsg: EventLogMsg = playerId => s"Now player $playerId balance is "
  ): Event

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
    * @param notMoneyErrMsg
    *   message visualized to the player if he has not money for buying the terrain
    * @return
    *   event that allows to player to buy terrain to became its owner
    */
  def BuyTerrainEvent(story: EventStory, notMoneyErrMsg: String): Event

  /** @param story
    *   event description, story choice will be united with a story interaction
    * @return
    *   Event that force to pay rent to the terrain owner
    */
  def GetRentEvent(story: EventStory): Event

  def BuildTokenEvent(
      terrainSelectionStory: String,
      tokenSelectionStory: String,
      numberOfTokenSelectionStory: String,
      notEnoughMoneyMsgErr: String
  ): Event

  def MortgageEvent(eventDescription: String): Event

  def RetrieveFromMortgageEvent(eventDescription: String, notMoneyErrMsg: String): Event
