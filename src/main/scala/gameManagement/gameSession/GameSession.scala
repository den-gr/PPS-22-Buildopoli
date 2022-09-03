package gameManagement.gameSession

import gameManagement.diceGenerator.Dice
import gameManagement.gameBank.Bank
import gameManagement.gameOptions.GameOptions
import gameManagement.gameStore.GameStore
import gameManagement.gameTurn.GameTurn
import org.slf4j.Logger
import lap.Lap
import player.Player
import terrain.Terrain

import scala.collection.mutable.ListBuffer

trait GameSession:
  def gameOptions: GameOptions
  def gameBank: Bank
  def gameTurn: GameTurn

  val gameStore: GameStore
  def gameLap: Lap
  def dice: Dice
  def logger: Logger
  def addOnePlayer(playerId: Option[Int]): Unit
  def addManyPlayers(n: Int): Unit
  def initializePlayer(lastPlayer: Player): Unit
  def setPlayerPosition(playerId: Int, newPosition: Int, isValidLap: Boolean): Unit
  def getPlayerPosition(playerId: Int): Int = gameStore.getPlayer(playerId).getPlayerPawnPosition
  def getPlayerTerrain(playerId: Int): Terrain = getTerrain(getPlayerPosition(playerId))

  export gameStore.getTerrain
object GameSession:
  def apply(
      gameOptions: GameOptions,
      gameBank: Bank,
      gameTurn: GameTurn,
      gameStore: GameStore,
      gameLap: Lap
  ): GameSession =
    GameSessionImpl(gameOptions: GameOptions, gameBank: Bank, gameTurn: GameTurn, gameStore: GameStore, gameLap: Lap)
