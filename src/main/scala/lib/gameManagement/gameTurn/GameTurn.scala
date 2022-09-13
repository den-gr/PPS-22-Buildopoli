package lib.gameManagement.gameTurn

import lib.gameManagement.gameOptions.GameOptions
import lib.gameManagement.gameStore.GameStore
import lib.player.Player

import scala.collection.mutable.ListBuffer

/** To manage turns of players in the game. Each player in his turn make actions, and launch dice. GameTurn identifies
  * the order in which players may make actions.
  */
trait GameTurn:
  /** List containing players that have already done the actual turn
    */
  var playerWithTurn: List[Int] = List()

  /** List of players blocked in doing some turns. Because of they are in Jail for example.
    */
  var blockingList: Map[Int, Int] = Map()

  /** @return
    *   the next players selected to play the game. Must be impossible to proceed if next turn is closed.
    */
  def selectNextPlayer(): Int

  /** @return
    *   if the next turn is possible. So if the inputList (into gameStore) is empty or not. That list must be empty
    *   because it contains all input values for the previous player
    */
  protected def isNextTurnOpen: Boolean

  /** @param playerId
    *   the selected player
    * @return
    *   if the given id has already played the game for the actual turn
    */
  def playerHasDoneTheActualTurn(playerId: Int): Boolean

  /** To block a player (ex. in prison)
    * @param playerId
    *   identifying one player
    * @param blockingTime
    *   number of turns to stay blocked
    */
  def lockPlayer(playerId: Int, blockingTime: Int): Unit

  /** To liberate player (from prison)
    * @param playerId
    *   identifying one player
    */
  def liberatePlayer(playerId: Int): Unit

  /** To reduce the time of blocked players
    */
  def doTurn(): Unit

  /** @param playerId
    *   identifying one player
    * @return
    *   remaining blocked movements for the given player. If blocked.
    */
  def getRemainingBlockedMovements(playerId: Int): Option[Int]

object GameTurn:
  def apply(gameOptions: GameOptions, gameStore: GameStore): GameTurn = DefaultGameTurn(gameOptions, gameStore)
