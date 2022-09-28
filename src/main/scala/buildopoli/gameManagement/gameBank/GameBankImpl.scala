package buildopoli.gameManagement.gameBank

import buildopoli.gameManagement.gameBank.bankDebit.{BankDebit, BankDebitImpl}
import buildopoli.gameManagement.gameOptions.GameOptions
import buildopoli.gameManagement.gameStore.GameStore
import buildopoli.player.Player

import scala.collection.mutable.ListBuffer

case class GameBankImpl(override val gameStore: GameStore) extends Bank:

  override val debitManagement: BankDebit = BankDebit()
  override def makeTransaction(senderId: Int, receiverId: Int, amount: Int): Unit = (senderId, receiverId) match
    case (0, _) => increasePlayerMoney(receiverId, amount)
    case (_, 0) => decreasePlayerMoney(senderId, amount)
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
    val debts = debitManagement.getDebitOfPlayer(playerId)
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

  private def playerHasEnoughMoney(player: Player, amount: Int): Boolean = getMoneyOfPlayer(player.playerId) > amount
