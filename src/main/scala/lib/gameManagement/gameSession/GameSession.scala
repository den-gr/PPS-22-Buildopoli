package lib.gameManagement.gameSession

import lib.behaviour.BehaviourExplorer
import lib.behaviour.BehaviourModule.Behaviour
import lib.gameManagement.diceGenerator.Dice
import lib.gameManagement.gameBank.Bank
import lib.gameManagement.gameOptions.GameOptions
import lib.gameManagement.gameStore.GameStore
import lib.gameManagement.gameTurn.GameTurn
import lib.gameManagement.log.GameLogger
import lib.lap.Lap
import lib.terrain.{Buildable, GroupManager, Terrain}

import scala.collection.mutable.ListBuffer

/** It represents the main controller of the game library. Encapsulating some logics about moving players, making
  * transactions, selecting terrains.
  */
trait GameSession:

  /** @return
    *   current instance of GameOptions
    */
  def gameOptions: GameOptions

  /** @return
    *   current instance of gameBank
    */
  def gameBank: Bank

  /** @return
    *   current instance of GameTurn
    */
  def gameTurn: GameTurn

  /** current instance of GameStore
    */
  val gameStore: GameStore

  /** @return
    *   current instance of gameLap
    */
  def gameLap: Lap

  /** @return
    *   current instance of the dice used in the actual game session
    */
  def dice: Dice

  /** @return
    *   current instance of the Logger used by all the game to log info in console
    */
  def logger: GameLogger

  /** To set a new position of a player, after launching the dice
    * @param playerId
    *   the player being moved
    * @param isValidLap
    *   to know if the player crosses the finish line because of some setbacks and so it should not have the reward of
    *   completing one lap.
    */
  def movePlayer(playerId: Int, isValidLap: Boolean = true, steps: Int = 0): Unit

  /** @param playerId
    *   to identify the player
    * @return
    *   the actual position of that player
    */
  def getPlayerPosition(playerId: Int): Int = gameStore.getPlayer(playerId).getPlayerPawnPosition

  /** Using getTerrain function exported by gameStore instance
    * @param playerId
    *   the actual player
    * @return
    *   the Terrain object of where the player is positioned
    */
  def getPlayerTerrain(playerId: Int): Terrain = getTerrain(getPlayerPosition(playerId))

  /** Used to start the game. Creating GroupManager and impeding creation of new terrains and players.
    */
  def startGame(): Unit

  /** @return
    *   current instance of GroupManager
    */
  def getGroupManager: GroupManager

  /** Give the access to the new behaviour explorer of the player that is a combination of global game behaviour and
    * local terrain behaviour that correspond to the player position
    * @param playerId
    *   id of player
    * @return
    *   behaviour explorer that allows to players interact with the game
    */
  def getFreshBehaviourExplorer(playerId: Int): BehaviourExplorer =
    Behaviour.combineExplorers(getPlayerTerrain(playerId).behaviour, gameStore.globalBehaviour, playerId)

  /** @return
    *   if game is ended or not
    */
  def isGameEnded: Boolean = this.gameStore.playersList.size == 1

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
