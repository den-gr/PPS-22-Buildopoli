package buildopoli.gameManagement.gameTurn

import buildopoli.endGame.EndGame
import buildopoli.gameManagement.gameOptions.GameOptions
import buildopoli.gameManagement.gameStore.GameStore
import buildopoli.player.Player

import scala.collection.mutable.ListBuffer

/** To manage turns of players in the game. Each player in his turn make actions, and launch dice. GameTurn identifies
  * the order in which players may make actions.
  */
trait GameTurn:
  /** List containing players that have already done the actual turn
    */
  var playerWithTurn: Seq[Int] = Seq()

  /** @return
    *   instance of EndGame to use into the game. EndGame uses strategy defined into game options.
    */
  def endGame: EndGame

  /** @return
    *   the next players selected to play the game. Must be impossible to proceed if next turn is closed.
    */
  def selectNextPlayer(): Int =
    this.checkToProceedWithNextTurn()
    this.verifyDefeatedPlayers()
    this.selectPlayer()

  /** Checks if is ok to proceed with next turn into the game. GameInputs list must be empty. PlayerWithTurn list must
    * be kept updated (also with defeated players).
    */
  def checkToProceedWithNextTurn(): Unit

  /** Must call DeleteDefeatedPlayers method into EndGame. To remove defeated players into the game, after each turn.
    * Uses EndGame instance.
    */
  def verifyDefeatedPlayers(): Unit

  /** @return
    *   the next player that must play the game. Using strategy "selector" provided into GameOptions.
    */
  def selectPlayer(): Int

object GameTurn:
  def apply(gameOptions: GameOptions, gameStore: GameStore): GameTurn =
    DefaultGameTurn(gameOptions, gameStore)
