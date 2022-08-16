package gameBank

import player.Player
import scala.collection.mutable.ListBuffer

trait Bank:
  def playersList: ListBuffer[Player]
  def debtsManagement: Boolean
  def makeTransaction(senderId: Int, receiverId: Int, amount: Int): Unit
  def increasePlayerMoney(playerId: Int, amount: Int): Unit
  def decreasePlayerMoney(playerId: Int, amount: Int): Unit
  def getDebtsList: Map[Int, Int]
  def getDebtsForPlayer(playerId: Int): Int
  def getMoneyForPlayer(playerId: Int): Int

object Bank:
  def apply(playersList: ListBuffer[Player], debtsManagement: Boolean): Bank =
    GameBankImpl(playersList, debtsManagement)
