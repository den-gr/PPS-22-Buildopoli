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

  def endGame: EndGame

  /** @return
    *   the next players selected to play the game. Must be impossible to proceed if next turn is closed.
    */
  def selectNextPlayer(): Int =
    verifyDefeatedPlayers()
    selectPlayer()

  def verifyDefeatedPlayers(): Unit

  def selectPlayer(): Int

  /** @return
    *   if the next turn is possible. So if the inputList (into gameStore) is empty or not. That list must be empty
    *   because it contains all input values for the previous player
    */
  protected def isNextTurnOpen: Boolean

object GameTurn:
  def apply(gameOptions: GameOptions, gameStore: GameStore): GameTurn =
    DefaultGameTurn(gameOptions, gameStore)
