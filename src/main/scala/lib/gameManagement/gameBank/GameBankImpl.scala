package lib.gameManagement.gameBank

import lib.gameManagement.gameBank.bankDebit.{BankDebit, BankDebitImpl}
import lib.gameManagement.gameOptions.GameOptions
import lib.gameManagement.gameStore.GameStore
import lib.player.Player

import scala.collection.mutable.ListBuffer

case class GameBankImpl(override val gameOptions: GameOptions, override val gameStore: GameStore) extends Bank:

  val debitManagement: BankDebit = BankDebitImpl()

  override def makeTransaction(senderId: Int, receiverId: Int, amount: Int): Unit = (senderId, receiverId) match
    case (0, _) => increasePlayerMoney(receiverId, amount)
    case (_, 0) => decreasePlayerMoney(senderId, amount)
    case (send, _) if getDebtsForPlayer(send) != 0 || getMoneyForPlayer(send) < amount =>
      throw new IllegalStateException("The sender has debit, so can only sell")
    case (send, receive) =>
      decreasePlayerMoney(send, amount)
      increasePlayerMoney(receive, amount)

  override def makeGlobalTransaction(senderId: Int, receiverId: Int, amount: Int): Unit = (senderId, receiverId) match
    case (0, _) =>
      gameStore.playersList.foreach(p =>
        decreasePlayerMoney(p.playerId, amount)
        increasePlayerMoney(receiverId, amount)
      )
    case (_, 0) =>
      gameStore.playersList.foreach(p =>
        decreasePlayerMoney(senderId, amount)
        increasePlayerMoney(p.playerId, amount)
      )

  def increasePlayerMoney(playerId: Int, amount: Int): Unit =
    val player: Player = gameStore.getPlayer(playerId)
    val debts = getDebtsForPlayer(playerId)
    if debts > 0 then this.increasePlayerMoneyWithDebts(debts, amount, player)
    else player.setPlayerMoney(player.getPlayerMoney + amount)

  def increasePlayerMoneyWithDebts(debit: Int, amount: Int, player: Player): Unit =
    if amount <= debit then this.debitManagement.decreaseDebit(player.playerId, amount)
    else
      this.debitManagement.decreaseDebit(player.playerId, debit)
      this.increasePlayerMoney(player.playerId, amount - debit)

  def decreasePlayerMoney(playerId: Int, amount: Int): Unit =
    val player: Player = gameStore.getPlayer(playerId)
    if playerHasEnoughMoney(player, amount) then player.setPlayerMoney(player.getPlayerMoney - amount)
    else
      this.debitManagement.increaseDebit(playerId, amount - player.getPlayerMoney)
      player.setPlayerMoney(0)

  override def getDebtsList: Map[Int, Int] = this.debitManagement.getDebtsList
  override def getDebtsForPlayer(playerId: Int): Int = this.debitManagement.getDebitForPlayer(playerId)
  def playerHasEnoughMoney(player: Player, amount: Int): Boolean = player.getPlayerMoney > amount
  override def getMoneyForPlayer(playerId: Int): Int = gameStore.getPlayer(playerId).getPlayerMoney
