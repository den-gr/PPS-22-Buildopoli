package gameBank

import player.Player

import scala.collection.mutable.ListBuffer

trait Bank:
  def makeTransaction(senderId: Int, receiverId: Int, amount: Int): Unit
  def setPlayerMoney(playerId: Int, amount: Int): Unit
  def getPlayersList: ListBuffer[Player]
