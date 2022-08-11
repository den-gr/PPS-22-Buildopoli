package behaviour

import org.scalatest.funsuite.AnyFunSuite
import BehaviourModule.*
import events.EventModule.*
import org.scalatest.*
import helper.TestMocks.*
import helper.TestMocks.BankMock.*

class BehaviourTest extends AnyFunSuite with BeforeAndAfterEach:

  var bank: BankMock = BankMock()

  override def beforeEach(): Unit =
    bank = BankMock()

  import Scenario.*
  val eventStrategy: () => Unit = () => bank.decrement(TAX)

  val jail: JailMock = JailMock()
  val player1: Int = 1
  val player2: Int = 2

  val BLOCKING_TIME = 3
  test("jailMock works in in the correct way ") {
    assert(jail.howManyTurnsPlayerIsBlocked(player1) == 0)
    assert(jail.howManyTurnsPlayerIsBlocked(player2) == 0)
    jail.blockPlayer(player1, BLOCKING_TIME)
    assert(jail.howManyTurnsPlayerIsBlocked(player1) == BLOCKING_TIME)
    assert(jail.howManyTurnsPlayerIsBlocked(player2) == 0)
    jail.doTurn()
    assert(jail.howManyTurnsPlayerIsBlocked(player1) == BLOCKING_TIME - 1)
    assert(jail.howManyTurnsPlayerIsBlocked(player2) == 0)
    jail.blockPlayer(player2, BLOCKING_TIME)
    jail.liberatePlayer(player1)
    assert(jail.howManyTurnsPlayerIsBlocked(player1) == 0)
    assert(jail.howManyTurnsPlayerIsBlocked(player2) == BLOCKING_TIME)
  }

  test("Jail behaviour imprison a player") {
    val imprisonStrategy: Int => Unit = jail.blockPlayer(_, BLOCKING_TIME)
    val story = EventStory(s"You are imprisoned for $BLOCKING_TIME turns", Seq("Wait liberation", "Prison break"))
    val imprisonEvent = Event(Scenario(imprisonStrategy, None, story))
    val behaviour: Behaviour = Behaviour(Seq(Seq(imprisonEvent)))
    println(behaviour.currentStories)
    behaviour.chooseEvent((0, 0))
    assertThrows[IllegalArgumentException](behaviour.chooseEvent(1, 0))
    assertThrows[IllegalArgumentException](behaviour.chooseEvent(0, 1))
  }
