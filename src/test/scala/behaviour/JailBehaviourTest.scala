package behaviour

import behaviour.BehaviourModule.Behaviour
import behaviour.BehaviourModule.Behaviour.*
import behaviour.event.EventModule.EventGroup
import behaviour.factory.BehaviourFactory
import gameManagement.gameBank.{Bank, GameBankImpl}
import gameManagement.gameOptions.GameOptions
import gameManagement.gameSession.{GameSession, GameSessionImpl}
import gameManagement.gameStore.{GameStore, GameStoreImpl}
import gameManagement.gameTurn.{DefaultGameTurn, GameTurn}
import lap.Lap.{GameLap, Lap, MoneyReward}
import org.scalatest.BeforeAndAfterEach
import org.scalatest.funsuite.AnyFunSuite
import player.Player
import util.GameSessionHelper.DefaultGameSession

import scala.util.control.Breaks.*
import scala.collection.mutable.ListBuffer

class JailBehaviourTest extends AnyFunSuite with BeforeAndAfterEach:

  private var gameTurn: GameTurn = _
  private var behaviour: Behaviour = _
  override def beforeEach(): Unit =
    val gameSession = DefaultGameSession()
    gameTurn = gameSession.gameTurn
    behaviour = BehaviourFactory(gameSession).JailBehaviour(BLOCKING_TIME)

  val BLOCKING_TIME = 2
  val PLAYER_1 = 1

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

  test("Escape event allow to the player escape from prison") {
    var liberated = false
    for i <- 0 to 100 if !liberated do
      var events = behaviour.getInitialEvents(i)
      chooseEvent(events)(i, (0, 0))
      gameTurn.doTurn()
      events = behaviour.getInitialEvents(i)
      assert(gameTurn.getRemainingBlockedMovements(i).nonEmpty)
      chooseEvent(events)(i, (0, 1))
      val remainingTurns = gameTurn.getRemainingBlockedMovements(i)
      if remainingTurns.isEmpty then
        liberated = true
        println(s"liberate at $i turn")
    if !liberated then assert(false)
  }
