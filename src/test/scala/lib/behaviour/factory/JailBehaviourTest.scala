package lib.behaviour.factory

import lib.behaviour.BehaviourModule.*
import lib.behaviour.BehaviourModule.Behaviour.*
import lib.behaviour.event.EventFactory.*
import lib.behaviour.event.{EventGroup, EventStoryModule}
import lib.behaviour.factory.BehaviourFactory
import lib.behaviour.factory.BehaviourFactory.*
import lib.behaviour.factory.input.JailBehaviourInput
import lib.gameManagement.gameTurn.GameTurn
import org.scalatest.BeforeAndAfterEach
import org.scalatest.funsuite.AnyFunSuite
import lib.util.GameSessionHelper.DefaultGameSession

class JailBehaviourTest extends AnyFunSuite with BeforeAndAfterEach:

  private var gameTurn: GameTurn = _
  private var behaviour: Behaviour = _
  override def beforeEach(): Unit =
    val gameSession = DefaultGameSession(100)
    gameTurn = gameSession.gameTurn
    behaviour = BehaviourFactory(gameSession).JailBehaviour()
    gameSession.startGame()

  val BLOCKING_TIME = 2
  val PLAYER_1: Int = 1

  test("Behaviour imprison a player") {
    var it = behaviour.getBehaviourIterator(PLAYER_1)
    assertThrows[IllegalArgumentException](it.next((1, 0)))
    assertThrows[IllegalArgumentException](it.next((0, 1)))

    it = behaviour.getBehaviourIterator(PLAYER_1)
    it.next()
    assert(!it.hasNext)
  }

  test("On the next turns player must be released from the Jail") {
    val it = behaviour.getBehaviourIterator(PLAYER_1)
    assert(gameTurn.getRemainingBlockedMovements(PLAYER_1).isEmpty)
    it.next()
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
      var it = behaviour.getBehaviourIterator(i)
      it.next()
      gameTurn.doTurn()
      it = behaviour.getBehaviourIterator(i)
      assert(gameTurn.getRemainingBlockedMovements(i).nonEmpty)
      it.next((0, 1))
      val remainingTurns = gameTurn.getRemainingBlockedMovements(i)
      if remainingTurns.isEmpty then
        liberated = true
        println(s"Player was liberated at $i turn")
    if !liberated then assert(false)
  }
