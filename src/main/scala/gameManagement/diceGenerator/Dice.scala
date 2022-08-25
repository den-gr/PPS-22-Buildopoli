package gameManagement.diceGenerator

import scala.util.Random

trait Dice:
  def random: Random
  def rollOneDice(): Int
  def rollMoreDice(nDice: Int): Int