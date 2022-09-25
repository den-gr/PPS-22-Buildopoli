package lib.gameManagement.gameBank

import lib.gameManagement.gameBank.bankDebit.{BankDebit, BankDebitImpl}
import lib.gameManagement.gameOptions.GameOptions
import lib.gameManagement.gameStore.GameStore
import lib.player.Player

import scala.collection.mutable.ListBuffer

trait Bank:

  /** @return
    *   the gameStore instance
    */
  def gameStore: GameStore

  /** Instance of BankDebit
    */
  val debitManagement: BankDebit

  /** To make transactions with two players. One of them could use default value "0", to represents the Bank.
    * @param senderId
    *   of the transaction
    * @param receiverId
    *   of the transaction
    * @param amount
    *   of the transaction
    */
  def makeTransaction(senderId: Int = 0, receiverId: Int = 0, amount: Int): Unit

  /** To make global transactions. One player can, requests some amount to all the other players. Or one player can send
    * some amount to all other players. Those transactions will complete also when some player has debit (It will be
    * increased)
    * @param receiverId
    *   of the transaction
    * @param amount
    *   of the transaction
    */
  def makeGlobalTransaction(senderId: Int = 0, receiverId: Int = 0, amount: Int): Unit

  /** @param playerId
    *   a specific player
    * @return
    *   money value
    */
  def getMoneyOfPlayer(playerId: Int): Int = gameStore.getPlayer(playerId).getPlayerMoney

  export debitManagement.getDebitOfPlayer
