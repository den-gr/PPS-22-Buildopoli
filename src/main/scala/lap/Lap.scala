package lap

import gameManagement.gameBank.Bank
import player.Player

object Lap :
  /**
   * It represents the reward given to the player that has completed a new lap
   */
  trait Reward:
    /**
     * It is the action performed to give the reward to the player
     * @param playerID the ID of the player that receives the reward
     */
    def triggerBonus(playerID: Int): Unit

  /**
   * Implementation of a reward that gift the player with a certain amount of money from the bank
   * @param money prize
   * @param bank that provides the money
   */
  case class MoneyReward(money: Int, bank: Bank) extends Reward:
    override def triggerBonus(playerID: Int): Unit = bank.increasePlayerMoney(playerID, money)

  /**
   * Buildopoli's terrains are displayed in circle, each player can complete a lap and gain a reward
  */
  trait Lap:
    /**
     * Says if the checked player has started a new lap and returns also the new player's position
     * @param isRewardable says if the movement is valid to take the reward. If it is explicitly said
     *                that it can not receive the reward or if the movement is backwards it is not valid
     * @param playerCurrentPosition the current position of the player
     * @param nSteps the number of steps the player has to take, if it is a positive number the player will move forward
     *               otherwise the player will move backwards
     * @param nCells the number of cells in the game board
     * @return the new position and a value that says if a new lap has started or not
     */
    def isNewLap(isRewardable: Boolean, playerCurrentPosition: Int, nSteps: Int, nCells: Int): (Int, Boolean)
    /**
     * Gives the reward for completing the lap to the player
     * @param playerID the player's ID
     */
    def giveReward(playerID: Int): Unit

  /**
   * A basic implementation of lap
   */
  case class GameLap(reward: Reward) extends Lap:
    override def isNewLap(isRewardable: Boolean, playerCurrentPosition: Int, nSteps: Int, nCells: Int): (Int, Boolean) = playerCurrentPosition + nSteps match
      case pos if pos > nCells => (pos - nCells, isRewardable)
      case pos if pos < 1 => (pos + nCells, false)
      case pos => (pos, false)

    override def giveReward(playerID: Int): Unit = reward.triggerBonus(playerID)

    def apply(reward: Reward): Lap = GameLap(reward: Reward)
