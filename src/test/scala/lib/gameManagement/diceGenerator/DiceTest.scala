package lib.gameManagement.diceGenerator

import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.read.ListAppender
import lib.gameManagement.diceGenerator.{Dice, SingleDice}
import org.scalatest.BeforeAndAfterEach
import org.scalatest.funsuite.AnyFunSuite
import org.slf4j.LoggerFactory

import scala.collection.mutable.ListBuffer

class DiceTest extends AnyFunSuite with BeforeAndAfterEach:
  val FACES: Int = 6
  var diceGenerator: Dice = _

  override def beforeEach(): Unit =
    diceGenerator = SingleDice(FACES)

  test("launching a single dice with 6 faces") {
    val array: ListBuffer[Int] = ListBuffer()
    for _ <- 0 to 10 do array += diceGenerator.rollOneDice()
    println(array)

    assert(array.forall(el => el > 0 && el <= 6))
  }

  test("launching multiple dice all with 6 faces") {
    val array: ListBuffer[Int] = ListBuffer()
    for _ <- 0 to 10 do array += diceGenerator.rollMoreDice(2)

    assert(array.forall(el => el > 0 && el <= 12))
  }
