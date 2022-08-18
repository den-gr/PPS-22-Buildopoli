package lap

object Lap :

  /**
   * It represent the reward given to the player that has completed a new lap
   */
  trait Reward:
    def triggerBonus(): Any

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
    def isNewLap(isValid: Boolean, playerID: Int, newPosition: Int): Boolean

    /**
     * Gives the reward for completing the lap to the player
     * @param playerID the player's ID
     * @param reward the reward
     */
    def giveReward(playerID: Int, reward: Reward): Any

  case class GameLap(playerPosition: Seq[Int]) extends Lap:
    override def isNewLap(isValid: Boolean, playerID: Int, newPosition: Int): Boolean = isValid && newPosition < playerPosition(playerID - 1)
    override def giveReward(playerID: Int, reward: Reward): Any = reward.triggerBonus()



