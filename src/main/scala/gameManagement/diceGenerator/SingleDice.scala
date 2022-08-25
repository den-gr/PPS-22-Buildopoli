package gameManagement.diceGenerator

import com.typesafe.scalalogging.Logger

import scala.util.Random

case class SingleDice(sides: Int) extends Dice:
  override val random: Random = new Random()
  var tempValue = 0
  val logger: Logger = Logger("SingleDice")
  override def rollOneDice(): Int =
    logger.info("Dice rolled")
    random.nextInt(sides) + 1

  override def rollMoreDice(nDice: Int): Int =
    rollDice(0, nDice)

  def rollDice(result: Int, nDice: Int): Int = nDice match
    case n if n > 0 =>
      logger.info("Dice n." + n + " rolling")
      this.tempValue = result
      rollDice(tempValue + rollOneDice(), nDice - 1)
    case _ =>
      logger.info("All dice rolled")
      result
    



