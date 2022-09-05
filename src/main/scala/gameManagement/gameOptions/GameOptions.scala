package gameManagement.gameOptions

import lap.Lap.Lap
import player.Player

import scala.collection.mutable.ListBuffer

/** @param playerInitialMoney
  *   how much money to give to each player at start
  * @param playerInitialCells
  *   how many cells to assign at each player at game start
  * @param nCells
  *   Setting number of cells for one game session
  * @param diceFaces
  *   Number of faces in the dice used to play the game
  * @param playerTurnSelector
  *   Lambda function used by GameTurn to select the next
  *   player that should play during the game
  */
case class GameOptions(playerInitialMoney: Int,
                       playerInitialCells: Int,
                       nCells: Int,
                       diceFaces: Int,
                       playerTurnSelector: (Seq[Player], Seq[Int]) => Int)
