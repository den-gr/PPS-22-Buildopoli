package gameBank

import gameOptions.Utils.getPlayer
import player.Player

import scala.collection.mutable.ListBuffer

case class GameBankImpl(override val playersList: ListBuffer[Player], override val debtsManagement: Boolean)
    extends Bank:

  val debitManagement: BankDebit = BankDebitImpl()

  override def makeTransaction(senderId: Int, receiverId: Int, amount: Int): Unit =
    decreasePlayerMoney(senderId, amount)
    increasePlayerMoney(receiverId, amount)

  override def increasePlayerMoney(playerId: Int, amount: Int): Unit =
    val player: Player = getPlayer(playerId, playersList)
    val debts = getDebtsForPlayer(playerId)
    if debtsManagement && debts > 0 then this.increasePlayerMoneyWithDebts(debts, amount, player)
    else player.setPlayerMoney(player.getPlayerMoney + amount)

  def increasePlayerMoneyWithDebts(debit: Int, amount: Int, player: Player): Unit =
    if amount <= debit then this.debitManagement.decreaseDebit(player.playerId, amount)
    else
      this.debitManagement.decreaseDebit(player.playerId, debit)
      this.increasePlayerMoney(player.playerId, amount - debit)

  override def decreasePlayerMoney(playerId: Int, amount: Int): Unit =
    val player: Player = getPlayer(playerId, playersList)
    if playerHasEnoughMoney(player, amount) then player.setPlayerMoney(player.getPlayerMoney - amount)
    else if debtsManagement then
      this.debitManagement.increaseDebit(playerId, amount - player.getPlayerMoney)
      player.setPlayerMoney(0)
    else
      throw new IllegalStateException("Money decrease not possible: player " + playerId + " does not have enough money")

  override def getDebtsList: Map[Int, Int] = this.debitManagement.getDebtsList
  override def getDebtsForPlayer(playerId: Int): Int = this.debitManagement.getDebitForPlayer(playerId)

  def playerHasEnoughMoney(player: Player, amount: Int): Boolean = player.getPlayerMoney > amount
  override def getMoneyForPlayer(playerId: Int): Int = getPlayer(playerId, playersList).getPlayerMoney
