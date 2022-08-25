package gameManagement.gameOptions

import lap.Lap.Lap
import player.Player

import scala.collection.mutable.ListBuffer

/** @param playerInitialMoney
  *   how much money to give to each player at start
  * @param playerInitialCells
  *   how many cells to assign at each player at game start
  * @param debtsManagement
  *   if you want to manage debit of each player during the game
  * @param nCells
  *   Setting number of cells for one game session
  * @param playerTurnSelector
  *   The function used by GameTurn to determine which is the next
  *   player that should play during the game
  */
case class GameOptions(playerInitialMoney: Int,
                       playerInitialCells: Int,
                       debtsManagement: Boolean,
                       nCells: Int,
                       diceFaces: Int,
                       playerTurnSelector: (ListBuffer[Player], ListBuffer[Int]) => Int)