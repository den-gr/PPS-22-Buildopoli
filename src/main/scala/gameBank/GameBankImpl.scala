package gameBank

import player.Player

import scala.collection.mutable.ListBuffer

case class GameBankImpl(playersList: ListBuffer[Player]) extends Bank:

  override def makeTransaction(senderId: Int, receiverId: Int, amount: Int): Unit =
    playersList
      .filter(p => p.getPlayerId.equals(senderId))
      .result()
      .head
      .setPlayerMoney(-amount)
    playersList
      .filter(p => p.getPlayerId.equals(receiverId))
      .result()
      .head
      .setPlayerMoney(amount)

  override def setPlayerMoney(playerId: Int, amount: Int): Unit =
    playersList
      .filter(p => p.getPlayerId.equals(playerId))
      .result()
      .head
      .setPlayerMoney(amount)

  override def getPlayersList: ListBuffer[Player] = this.playersList
