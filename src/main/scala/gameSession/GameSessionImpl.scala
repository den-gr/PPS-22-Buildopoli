package gameSession

import gameBank.{Bank, GameBankImpl}
import gameOptions.GameOptions
import lap.Lap.{Lap, Reward}
import player.{Player, PlayerImpl}

import scala.collection.mutable.ListBuffer

case class GameSessionImpl(override val gameOptions: GameOptions, override val gameLap: Lap)
    extends GameSession:

  var playersList: ListBuffer[Player] = ListBuffer()
  var playerIdsCounter: Int = 0
  override val gameBank: Bank = GameBankImpl(playersList, gameOptions.debtsManagement)

  override def addManyPlayers(n: Int): Unit =
    for _ <- 0 until n do this.addOnePlayer(Option.empty)

  override def addOnePlayer(playerId: Option[Int]): Unit =
    this.playersList += PlayerImpl(this.checkPlayerId(playerId))
    if playerId.isEmpty then this.playerIdsCounter += 1
    this.initializePlayer()

  override def initializePlayer(): Unit =
    this.playersList.last.setPlayerMoney(gameOptions.playerMoney)

  def checkPlayerId(playerId: Option[Int]): Int =
    playerId match
      case None =>
        while playerIdAlreadyExist(this.playerIdsCounter) do this.playerIdsCounter += 1
        this.playerIdsCounter
      case Some(id) =>
        var player = id
        while playerIdAlreadyExist(player) do player += 1
        player

  def playerIdAlreadyExist(playerId: Int): Boolean =
    this.playersList.exists(p => p.playerId.equals(playerId))

  override def setPlayerPosition(playerId: Int, nPositions: Int, isValidLap: Boolean, lapReward: Reward): Unit = ???
//    val player: Player = getPlayer(playerId)
//    val gameLapResult = this.gameLap.isNewLap(isValidLap, player.getPlayerPawnPosition, nPositions, gameOptions.nCells)
//    player.setPlayerPawnPosition(gameLapResult._1)
//    if gameLapResult._2 then this.gameLap.giveReward(playerId, lapReward)

  def getPlayer(playerId: Int): Player = playersList
    .filter(p => p.playerId.equals(playerId))
    .result()
    .head

  override def getPlayersList: ListBuffer[Player] = this.playersList