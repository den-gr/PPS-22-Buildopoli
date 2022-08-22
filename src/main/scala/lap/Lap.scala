package lap

import gameBank.Bank
import player.Player

object Lap :

  /**
   * It represent the reward given to the player that has completed a new lap
   */
  trait Reward:
    def triggerBonus(playerID: Int): Unit

  case class MoneyReward(bank: Bank, money: Int) extends Reward:
    override def triggerBonus(playerID: Int): Unit = bank.increasePlayerMoney(playerID, money)

  /**
   * Buildopoli's terrains are displayed in circle, each player can complete a lap and gain a reward
  */
  trait Lap:
    /**
     * Says if the checked player has started a new lap
     * @param isValid says if the movement is valid to gain the reward
     * @param playerID checked player's ID
     * @param newPosition the new player's position
     * @return a Boolean value that says if the player has started a new lap
     */
    def isNewLap(isValid: Boolean, playerCurrentPosition: Int, nSteps: Int, nCells: Int): (Int, Boolean)

    /**
     * Gives the reward for completing the lap to the player
     * @param playerID the player's ID
     * @param reward the reward
     */
    def giveReward(playerID: Int, reward: Reward): Unit

  /**
   * A basic implementation of lap
   * @param playerPosition the Seq of player's positions
   */
  case class GameLap() extends Lap:
    override def isNewLap(isValid: Boolean, playerCurrentPosition: Int, nSteps: Int, nCells: Int): (Int, Boolean) = playerCurrentPosition + nSteps > nCells match
      case true => (playerCurrentPosition + nSteps - nCells, isValid)
      case false => (playerCurrentPosition + nSteps, false)

    override def giveReward(playerID: Int, reward: Reward): Unit = reward.triggerBonus(playerID)



