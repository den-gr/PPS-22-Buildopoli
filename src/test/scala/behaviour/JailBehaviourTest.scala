package behaviour

import behaviour.BehaviourModule.Behaviour
import behaviour.BehaviourModule.Behaviour.*
import behaviour.event.EventModule.EventGroup
import behaviour.factory.BehaviourFactory
import gameManagement.gameTurn.GameTurn
import org.scalatest.BeforeAndAfterEach
import org.scalatest.funsuite.AnyFunSuite
import util.GameSessionHelper.DefaultGameSession

class JailBehaviourTest extends AnyFunSuite with BeforeAndAfterEach:

  private var gameTurn: GameTurn = _
  private var behaviour: Behaviour = _
  override def beforeEach(): Unit =
    val gameSession = DefaultGameSession()
    gameTurn = gameSession.gameTurn
    behaviour = BehaviourFactory(gameSession).JailBehaviour()

  val BLOCKING_TIME = 2
  val PLAYER_1: Int = 1

  test("Behaviour imprison a player") {
    val events = behaviour.getInitialEvents(PLAYER_1)
    assertThrows[IllegalArgumentException](chooseEvent(events)(PLAYER_1, (1, 0)))
    assertThrows[IllegalArgumentException](chooseEvent(events)(PLAYER_1, (0, 1)))
    val nextEvents: Seq[EventGroup] = chooseEvent(events)(PLAYER_1, (0, 0))
    assert(nextEvents.isEmpty)
  }

  test("On the next turns player must be released from the Jail") {
    val events = behaviour.getInitialEvents(PLAYER_1)
    assert(gameTurn.getRemainingBlockedMovements(PLAYER_1).isEmpty)
    chooseEvent(events)(PLAYER_1, (0, 0))
    assert(gameTurn.getRemainingBlockedMovements(PLAYER_1).get == BLOCKING_TIME)
    gameTurn.doTurn()
    assert(gameTurn.getRemainingBlockedMovements(PLAYER_1).get == BLOCKING_TIME - 1)
    gameTurn.doTurn()
    gameTurn.doTurn()
    assert(gameTurn.getRemainingBlockedMovements(PLAYER_1).isEmpty)
  }

  // this test can give a false negative result with probability (5/6)^100 = 7.6532335e-78 if the dice have 6 sides
  test("Escape event allow to the player escape from prison") {
    var liberated = false
    for i <- 1 to 100 if !liberated do
      var events = behaviour.getInitialEvents(i)
      chooseEvent(events)(i, (0, 0))
      gameTurn.doTurn()
      events = behaviour.getInitialEvents(i)
      assert(gameTurn.getRemainingBlockedMovements(i).nonEmpty)
      chooseEvent(events)(i, (0, 1))
      val remainingTurns = gameTurn.getRemainingBlockedMovements(i)
      if remainingTurns.isEmpty then
        liberated = true
        println(s"Player was liberated at $i turn")
    if !liberated then assert(false)
  }
