package gameBank

import player.Player
import scala.collection.mutable.ListBuffer

case class GameBankImpl(playersList: ListBuffer[Player], debtsManagement: Boolean) extends Bank:

  val debitManagement: BankDebit = BankDebitImpl()

  override def makeTransaction(senderId: Int, receiverId: Int, amount: Int): Unit =
    decreasePlayerMoney(senderId, amount)
    increasePlayerMoney(receiverId, amount)

  override def increasePlayerMoney(playerId: Int, amount: Int): Unit =
    val player: Player = getPlayer(playerId)
    val debts = getDebtsForPlayer(playerId)
    if debtsManagement && debts > 0 then this.increasePlayerMoneyWithDebts(debts, amount, player)
    else player.increaseMoney(amount)

  def increasePlayerMoneyWithDebts(debit: Int, amount: Int, player: Player): Unit =
    if amount <= debit then this.debitManagement.decreaseDebit(player.getPlayerId, amount)
    else
      this.debitManagement.decreaseDebit(player.getPlayerId, debit)
      this.increasePlayerMoney(player.getPlayerId, amount - debit)

  override def decreasePlayerMoney(playerId: Int, amount: Int): Unit =
    val player: Player = getPlayer(playerId)
    if playerHasEnoughMoney(player, amount) then player.decreaseMoney(amount)
    else
      if debtsManagement then this.debitManagement.increaseDebit(playerId, amount - player.getPlayerMoney)
      player.setMoney(0)

  override def getDebtsList: Map[Int, Int] = this.debitManagement.getDebitList()
  override def getDebtsForPlayer(playerId: Int): Int = this.debitManagement.getDebitForPlayer(playerId)
  def getPlayer(playerId: Int): Player = playersList
    .filter(p => p.getPlayerId.equals(playerId))
    .result()
    .head

  def playerHasEnoughMoney(player: Player, amount: Int): Boolean = player.getPlayerMoney > amount
  override def getMoneyForPlayer(playerId: Int): Int = getPlayer(playerId).getPlayerMoney
