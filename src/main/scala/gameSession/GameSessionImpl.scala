package gameSession

import gameBank.{Bank, GameBankImpl}
import gameOptions.{GameOptions, GameTemplate}
import player.{Player, PlayerImpl}

import scala.collection.mutable.ListBuffer

case class GameSessionImpl(override val gameOptions: GameOptions, override val gameTemplate: GameTemplate)
    extends GameSession:

  var playersList: ListBuffer[Player] = ListBuffer()
  var playerIdsCounter: Int = 0
  val debtsManagement: Boolean = true
  override val gameBank: Bank = GameBankImpl(playersList, debtsManagement)

  override def addManyPlayers(n: Int): Unit =
    for _ <- 0 until n do this.addOnePlayer(Option.empty)

  override def addOnePlayer(playerId: Option[Int]): Unit = playerId match
    case None =>
      this.playersList += PlayerImpl(this.checkPlayerIdCounter())
      this.playerIdsCounter += 1
    case Some(id) =>
      this.playersList += PlayerImpl(this.checkGivenPlayerId(id))

  def checkPlayerIdCounter(): Int =
    while playerIdAlreadyExist(this.playerIdsCounter) do this.playerIdsCounter += 1
    this.playerIdsCounter

  def checkGivenPlayerId(id: Int): Int =
    var player = id
    while playerIdAlreadyExist(player) do player += 1
    player

  def playerIdAlreadyExist(playerId: Int): Boolean =
    this.playersList.exists(p => p.playerId.equals(playerId))

  override def getPlayersList: ListBuffer[Player] = this.playersList
