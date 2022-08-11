package gameSession

import gameBank.{Bank, GameBankImpl}
import player.{Player, PlayerImpl}

import scala.collection.mutable.ListBuffer

// TODO: Take in input GameOptions and GameTemplate
// TODO: Method initialize taking in input info from GameOptions
case class GameSessionImpl() extends GameSession:

  var playersList: ListBuffer[Player] = ListBuffer()
  var playerIdsCounter: Int = 0
  val debtsManagement: Boolean = true
  val gameBank: Bank = GameBankImpl(playersList, debtsManagement)

  override def addManyPlayers(n: Int): Unit =
    for _ <- 0 until n do this.addOnePlayer(Option.empty)

  override def addOnePlayer(playerId: Option[Int]): Unit = playerId match
    case None =>
      this.checkPlayerIdCounter()
      this.playersList += PlayerImpl(this.playerIdsCounter)
      this.playerIdsCounter += 1
    case Some(id) =>
      var player: Int = id
      while playerIdAlreadyExist(id) do player += 1
      this.playersList += PlayerImpl(player)

  def checkPlayerIdCounter(): Unit = while playerIdAlreadyExist(this.playerIdsCounter) do this.playerIdsCounter += 1

  def playerIdAlreadyExist(playerId: Int): Boolean =
    this.playersList.exists(p => p.getPlayerId.equals(playerId))

  override def getPlayersList: ListBuffer[Player] = this.playersList

  override def getGameBank: Bank = this.gameBank
