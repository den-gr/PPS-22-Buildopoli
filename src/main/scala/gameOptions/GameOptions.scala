package gameOptions

import lap.Lap.Reward
import player.Player

import scala.collection.mutable.ListBuffer

/** @param playerMoney
  *   how much money to give to each player at start
  * @param playerCells
  *   how many cells to assign at each player at game start
  * @param debtsManagement
  *   if you want to manage debit of each player during the game
  * @param nCells
  *   Setting number of cells for one game session
  * @param lapReward
  *   The reward given to each player when he completes one lap
  * @param selector
  *   The function used by GameTurn to determine which is the next
  *   player that should play during the game
  */
case class GameOptions(playerMoney: Int,
                       playerCells: Int,
                       debtsManagement: Boolean,
                       nCells: Int,
                       lapReward: Reward,
                       selector: (ListBuffer[Player], ListBuffer[Int]) => Int)
