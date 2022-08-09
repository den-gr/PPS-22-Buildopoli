package gameSession

import gameBank.{Bank, GameBankImpl}
import player.{Player, PlayerImpl}
import todo.*

import scala.collection.mutable.ListBuffer

case class GameSessionImpl(gameOptions: GameOptions, gameTemplate: GameTemplate) extends GameSession:
  var playersList: ListBuffer[Player] = ListBuffer()
  var playerIdsCounter: Int = 0
  var gameBank: Bank = GameBankImpl(playersList)

  /** TODO: param n should be limited to max players number in gameOptions TODO: when a user is being created, bank
    * should make a transaction to give initial money TODO: when a user is being created, his ID should be assigned to
    * some cells that he initially owns TODO: all those parameters are specified onto gameOptions obj
    */
  override def addManyPlayers(n: Int): Unit =
    for _ <- 0 until n do
      this.playersList += PlayerImpl(this.playerIdsCounter)
      this.playerIdsCounter += 1

  override def addOnePlayer(playerId: Option[Int]): Unit = playerId match
    case None => 
      this.playersList += PlayerImpl(this.playerIdsCounter)
      this.playerIdsCounter += 1
    case Some(id) => this.playersList += PlayerImpl(id)

  override def getPlayersList: ListBuffer[Player] = this.playersList

  override def getGameBank: Bank = this.gameBank
