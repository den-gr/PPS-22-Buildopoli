package gameManagement.diceGenerator

import org.slf4j.{Logger, LoggerFactory}

import scala.util.Random

case class SingleDice(sides: Int) extends Dice:
  override val random: Random = new Random()
  var tempValue = 0
  val logger: Logger = LoggerFactory.getLogger(this.getClass)

  override def rollOneDice(): Int =
    val ris = random.nextInt(sides) + 1
    logger.info(s"Dice rolled -> $ris")
    ris

  override def rollMoreDice(nDice: Int): Int =
    rollDice(0, nDice)

  def rollDice(result: Int, nDice: Int): Int = nDice match
    case n if n > 0 =>
      this.tempValue = result
      rollDice(tempValue + rollOneDice(), nDice - 1)
    case _ =>
      logger.info(s"Dices sum -> $result")
      result
