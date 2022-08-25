package gameManagement.diceGenerator

import org.scalatest.funsuite.AnyFunSuite

import scala.collection.mutable.ListBuffer

class DiceTest extends AnyFunSuite:
  val FACES: Int = 6
  val diceGenerator: Dice = SingleDice(FACES)

  test("launching a single dice with 6 faces") {
    val array : ListBuffer[Int] = ListBuffer()
    for _ <- 0 to 10 do
      diceGenerator.rollOneDice()
      array += diceGenerator.value

    assert(array.forall(el => el > 0 && el <= 6))
  }

  test("launching multiple dice all with 6 faces") {
    val array: ListBuffer[Int] = ListBuffer()
    for _ <- 0 to 10 do
      diceGenerator.rollMoreDice(2)
      array += diceGenerator.value

    assert(array.forall(el => el > 0 && el <= 12))
  }
