package gameManagement.diceGenerator

import scala.util.Random

case class SingleDice(sides: Int) extends Dice:
  override val random: Random = new Random()

  override def rollOneDice(): Unit =
    this.value = random.nextInt(sides) + 1

  override def rollMoreDice(nDice: Int): Unit =
    this.value = rollDice(0, nDice)

  def rollDice(tempVal: Int, nDice: Int): Int = nDice match
    case n if n > 0 => 
      val tempValue = this.value
      rollOneDice()
      rollDice(tempValue + this.value, nDice - 1)
    case _ => tempVal
    



