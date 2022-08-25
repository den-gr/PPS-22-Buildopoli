package gameManagement.diceGenerator

import scala.util.Random

trait Dice:
  def random: Random
  var value: Int = 0
  def rollOneDice(): Unit
  def rollMoreDice(nDice: Int): Unit