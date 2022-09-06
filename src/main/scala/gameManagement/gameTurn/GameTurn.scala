package gameManagement.gameTurn

import gameManagement.gameOptions.GameOptions
import player.Player

import scala.collection.mutable.ListBuffer

/**
 * To manage turns of players in the game.
 * Each player in his turn make actions, and launch dice.
 * GameTurn identifies the order in which players may make actions.
 */
trait GameTurn:
  /**
   * List containing players that have already done the actual turn
   */
  var playerWithTurn: List[Int] = List()

  /**
   * List of players blocked in doing some turns.
   * Because of they are in Jail for example.
   */
  var blockingList: Map[Int, Int] = Map()

  /**
   * @return the next players selected to play the game
   */
  def selectNextPlayer(): Int

  /**
   * @param playerId the selected player
   * @return if the given id has already played the game for the actual turn
   */
  def playerHasDoneTheActualTurn(playerId: Int): Boolean

  /**
   * To block a player (ex. in prison)
   * @param playerId identifying one player
   * @param blockingTime number of turns to stay blocked
   */
  def lockPlayer(playerId: Int, blockingTime: Int): Unit

  /**
   * To liberate player (from prison)
   * @param playerId identifying one player
   */
  def liberatePlayer(playerId: Int): Unit

  /**
   * To reduce the time of blocked players
   */
  def doTurn(): Unit

  /**
   *
   * @param playerId identifying one player
   * @return remaining blocked movements for the given player. If blocked.
   */
  def getRemainingBlockedMovements(playerId: Int): Option[Int]

