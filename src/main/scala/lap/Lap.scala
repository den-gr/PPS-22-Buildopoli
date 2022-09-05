package lap

import gameManagement.gameBank.Bank
import player.Player

object Lap :

  /**
   * It represent the reward given to the player that has completed a new lap
   */
  trait Reward:
    /**
     * It is the action performed to give the reward to the player
     * @param playerID the ID of the player that receives the reward
     */
    def triggerBonus(playerID: Int): Unit

  case class MoneyReward(money: Int, bank: Bank) extends Reward:
    override def triggerBonus(playerID: Int): Unit =
      bank.makeTransaction(receiverId = playerID, amount = money)

  /**
   * Buildopoli's terrains are displayed in circle, each player can complete a lap and gain a reward
  */
  trait Lap:
    def reward: Reward

    /**
     * Says if the checked player has started a new lap and returns also the new player's position
     * @param isValid says if the movement is valid to take the reward. If it is explicitly said
     *                that it can not receive the reward or if the movement is backwards it is not valid
     * @param playerCurrentPosition the current position of the player
     * @param nSteps the number of additional steps the player has to take
     * @param nCells the number of cells in the game board
     * @return the new position and a value that says if a new lap has started or not
     */
    def isNewLap(isValid: Boolean, playerCurrentPosition: Int, nSteps: Int, nCells: Int): (Int, Boolean)

    /**
     * Gives the reward for completing the lap to the player
     * @param playerID the player's ID
     */
    def giveReward(playerID: Int): Unit

  /**
   * A basic implementation of lap
   */
  case class GameLap(override val reward: Reward) extends Lap:
    override def isNewLap(isValid: Boolean, playerCurrentPosition: Int, nSteps: Int, nCells: Int): (Int, Boolean) = playerCurrentPosition + nSteps > nCells match
      case true => (playerCurrentPosition + nSteps - nCells, isValid)
      case false => (playerCurrentPosition + nSteps, false)

    override def giveReward(playerID: Int): Unit = reward.triggerBonus(playerID)

  def apply(reward: Reward): Lap = GameLap(reward: Reward)
