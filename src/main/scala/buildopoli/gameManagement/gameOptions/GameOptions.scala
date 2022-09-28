package buildopoli.gameManagement.gameOptions

import buildopoli.endGame.EndGame
import buildopoli.player.Player

import scala.collection.mutable.ListBuffer

/** @param playerInitialMoney
  *   how much money to give to each player at start
  * @param playerInitialCells
  *   how many cells to assign at each player at game start
  * @param nUsers
  *   Setting number of users in the game
  * @param diceFaces
  *   Number of faces in the dice used to play the game
  * @param playerTurnSelector
  *   Lambda function used by GameTurn to select the next player that should play during the game
  */
case class GameOptions(
    playerInitialMoney: Int,
    playerInitialCells: Int,
    nUsers: Int,
    diceFaces: Int,
    playerTurnSelector: (Seq[Player], Seq[Int]) => Int,
    removePlayerStrategy: Player => Boolean = _ => false
)
