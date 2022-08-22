package gameOptions

/** @param playerMoney
  *   how much money to give to each player at start
  * @param playerCells
  *   how many cells to assign at each player at game start
  * @param debtsManagement
  *   if you want to manage debit of each player during the game
  */
case class GameOptions(playerMoney: Int, playerCells: Int, debtsManagement: Boolean)
