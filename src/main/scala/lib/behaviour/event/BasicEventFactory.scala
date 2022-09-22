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

  /** @param terrainSelectionStory
    *   explain to player that he must select a terrain where will be built a token (building)
    * @param tokenSelectionStory
    *   explain to player that he must select a building type to build
    * @param numberOfTokenSelectionStory
    *   explain to player that he must select the number of tokens ot build
    * @param notEnoughMoneyMsgErr
    *   error message that will be returned to the player if he have not money to build the a token
    * @return
    *   event that allows to user build token in any its terrain that is in a group
    */
  def BuildTokenEvent(
      terrainSelectionStory: String,
      tokenSelectionStory: String,
      numberOfTokenSelectionStory: String,
      notEnoughMoneyMsgErr: String
  ): Event

  /** @param eventDescription
    *   explain to the player can give in mortgage its terrain for give some money
    * @return
    *   event that allows to block an owned terrain and receive a compensation from the bank
    */
  def MortgageEvent(eventDescription: String): Event

  /** @param eventDescription
    *   explain to the player that he can retrieve its terrain from mortgage by paying some money
    * @param notMoneyErrMsg
    *   error message that will be returned to the player if he have not money to retrieve the terrain from mortgage
    * @return
    *   event that allows retrieve a terrain from mortgage
    */
  def RetrieveFromMortgageEvent(eventDescription: String, notMoneyErrMsg: String): Event
