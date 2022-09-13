package lib.gameManagement.gameBank

import lib.gameManagement.gameOptions.GameOptions
import lib.gameManagement.gameStore.GameStore
import lib.player.Player

import scala.collection.mutable.ListBuffer

trait Bank:

  /**
   * @return the gameOptions instance
   */
  def gameOptions: GameOptions

  /**
   * @return the gameStore instance
   */
  def gameStore: GameStore

  /**
   * To make transactions with two players. Sender must not have debit, to complete successfully.
   * @param senderId of the transaction
   * @param receiverId of the transaction
   * @param amount of the transaction
   */
  def makeTransaction(senderId: Int = 0, receiverId: Int = 0, amount: Int): Unit

  /**
   * To make global transactions. 
   * One player can, requests some amount to all the other players.
   * Or one player can send some amount to all other players.
   * Those transactions will complete also when some player has debit. It will be increased.
   * @param receiverId of the transaction
   * @param amount of the transaction
   */
  def makeGlobalTransaction(senderId: Int = 0, receiverId: Int = 0, amount: Int): Unit

  /**
   * @return a map[key, value] = [playerId, debit value]
   */
  def getDebtsList: Map[Int, Int]

  /**
   * @param playerId to identify one player
   * @return debit value for a specific player
   */
  def getDebtsForPlayer(playerId: Int): Int

  /**
   * @param playerId a specific player
   * @return money value
   */
  def getMoneyForPlayer(playerId: Int): Int

object Bank:
  def apply(gameOptions: GameOptions, gameStore: GameStore): Bank =
    GameBankImpl(gameOptions: GameOptions, gameStore: GameStore)
