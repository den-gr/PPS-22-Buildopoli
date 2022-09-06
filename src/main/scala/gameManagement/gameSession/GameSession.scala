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

/**
 * It represents the main controller of the game library.
 * Encapsulating some logics about moving players, making transactions, selecting terrains.
 */
trait GameSession:

  /**
   * @return current instance of GameOptions
   */
  def gameOptions: GameOptions

  /**
   * @return current instance of gameBank
   */
  def gameBank: Bank

  /**
   * @return current instance of GameTurn
   */
  def gameTurn: GameTurn

  /**
   * current instance of gameStore. Made val type, necessary to export some methods.
   */
  val gameStore: GameStore

  /**
   * @return current instance of gameLap
   */
  def gameLap: Lap

  /**
   * @return current instance of the dice used in the actual game session
   */
  def dice: Dice

  /**
   * @return current instance of the Logger used by all the game to log info in console
   */
  def logger: Logger

  /**
   * To add a single player in the game
   * @param playerId, optionally used to assign a specific ID to a player being created
   */
  def addOnePlayer(playerId: Option[Int]): Unit

  /**
   * To add multiple players at one time
   * @param n players
   */
  def addManyPlayers(n: Int): Unit

  /**
   * To set a new position of a player, after launching the dice for example
   * @param playerId the player being moved
   * @param nSteps that the player makes
   * @param isValidLap to know if the player crosses the finish line because of
   *                   some setbacks and so it should not have the reward of completing one lap.
   */
  def setPlayerPosition(playerId: Int, nSteps: Int, isValidLap: Boolean = true): Unit

  /**
   * @param playerId to identify the player
   * @return the actual position of that player
   */
  def getPlayerPosition(playerId: Int): Int = gameStore.getPlayer(playerId).getPlayerPawnPosition

  /**
   * Using getTerrain function exported by gameStore instance
   * @param playerId the actual player
   * @return the Terrain object of where the player is positioned
   */
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
