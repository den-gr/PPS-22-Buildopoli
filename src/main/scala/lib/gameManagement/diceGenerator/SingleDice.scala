package lib.gameManagement.diceGenerator

import lib.gameManagement.log.GameLogger
import org.slf4j.{Logger, LoggerFactory}

import scala.util.Random

case class SingleDice(sides: Int, logger: GameLogger) extends Dice:
  override val random: Random = new Random()
  var tempValue = 0

  override def rollOneDice(): Int =
    val res = random.nextInt(sides) + 1
    logger.log(s"Dice rolled -> $res")
    res

  override def rollMoreDice(nDice: Int): Int =
    rollDice(0, nDice)

  def rollDice(result: Int, nDice: Int): Int = nDice match
    case n if n > 0 =>
      this.tempValue = result
      rollDice(tempValue + rollOneDice(), nDice - 1)
    case _ =>
      logger.log(s"Dices sum -> $result")
      result
