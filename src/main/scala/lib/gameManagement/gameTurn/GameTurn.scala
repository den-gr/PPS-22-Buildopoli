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

object GameTurn:
  def apply(gameOptions: GameOptions, gameStore: GameStore): GameTurn = DefaultGameTurn(gameOptions, gameStore)
