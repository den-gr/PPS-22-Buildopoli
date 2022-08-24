package gameManagement.gameBank

import gameManagement.gameOptions.GameOptions
import gameManagement.gameStore.GameStore
import player.Player

import scala.collection.mutable.ListBuffer

trait Bank:
  def gameOptions: GameOptions
  def gameStore: GameStore
  def makeTransaction(senderId: Int, receiverId: Int, amount: Int): Unit
  def increasePlayerMoney(playerId: Int, amount: Int): Unit
  def decreasePlayerMoney(playerId: Int, amount: Int): Unit
  def getDebtsList: Map[Int, Int]
  def getDebtsForPlayer(playerId: Int): Int
  def getMoneyForPlayer(playerId: Int): Int

object Bank:
  def apply(gameOptions: GameOptions, gameStore: GameStore): Bank =
    GameBankImpl(gameOptions: GameOptions, gameStore: GameStore)
