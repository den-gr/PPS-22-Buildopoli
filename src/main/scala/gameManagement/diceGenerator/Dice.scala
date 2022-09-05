package gameManagement.diceGenerator

import scala.util.Random

/**
 * It represents one Dice, launched when we need to know the next position of one player
 */
trait Dice:

  /**
   * @return a random generator based on the number of faces in the dice
   */
  def random: Random

  /**
   * Used to launch a single dice during the game
   * @return the value taken from the random generator
   */
  def rollOneDice(): Int

  /**
   * Launches more than one Dice (using recursively the rollOneDice method)
   * @param nDice, how many dice to launch
   * @return
   */
  def rollMoreDice(nDice: Int): Int