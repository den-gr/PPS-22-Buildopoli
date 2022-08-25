package gameManagement.gameSession

import gameManagement.diceGenerator.{Dice, SingleDice}
import gameManagement.gameBank.{Bank, GameBankImpl}
import gameManagement.gameOptions.GameOptions
import gameManagement.gameStore.GameStore
import gameManagement.gameTurn.GameTurn
import lap.Lap.Lap
import org.slf4j.{Logger, LoggerFactory}
import player.{Player, PlayerImpl}

import scala.collection.mutable.ListBuffer

case class GameSessionImpl(override val gameOptions: GameOptions,
                           override val gameBank: Bank,
                           override val gameTurn: GameTurn,
                           override val gameStore: GameStore,
                           override val gameLap: Lap)
    extends GameSession:
  
  override val dice: Dice = SingleDice(gameOptions.diceFaces)

  val logger: Logger = LoggerFactory.getLogger("GameSession")

  override def addManyPlayers(n: Int): Unit =
    for _ <- 0 until n do this.addOnePlayer(Option.empty)

  override def addOnePlayer(playerId: Option[Int]): Unit =
    if playerId.isEmpty then this.gameStore.playerIdsCounter += 1
    this.gameStore.playersList += PlayerImpl(this.checkPlayerId(playerId))
    this.initializePlayer(this.gameStore.playersList.last)

  override def initializePlayer(lastPlayer: Player): Unit =
    lastPlayer.setPlayerMoney(gameOptions.playerInitialMoney)

  def checkPlayerId(playerId: Option[Int]): Int =
    playerId match
      case None =>
        while playerIdAlreadyExist(this.gameStore.playerIdsCounter) do this.gameStore.playerIdsCounter += 1
        this.gameStore.playerIdsCounter
      case Some(id) =>
        var player = id
        while playerIdAlreadyExist(player) do player += 1
        player

  def playerIdAlreadyExist(playerId: Int): Boolean =
    this.gameStore.playersList.exists(p => p.playerId.equals(playerId))

  override def setPlayerPosition(playerId: Int, nSteps: Int, isValidLap: Boolean): Unit =
    val player = gameStore.getPlayer(playerId)
    val result = gameLap.isNewLap(isValidLap, player.getPlayerPawnPosition, nSteps, gameOptions.nCells)
    player.setPlayerPawnPosition(result._1)
    if result._2 then gameLap.giveReward(playerId)
    
  


