package behaviour

import behaviour.BehaviourModule.{Behaviour, StoryGroup}
import events.EventModule.{Event, EventStory, Scenario}
import helper.TestMocks.JailMock
import org.scalatest.BeforeAndAfterEach
import org.scalatest.funsuite.AnyFunSuite

class JailTest extends AnyFunSuite with BeforeAndAfterEach:
  var jail: JailMock = JailMock()
  val PLAYER_1: Int = 1
  val PLAYER_2: Int = 2

  override def beforeEach(): Unit =
    jail = JailMock()

  val BLOCKING_TIME = 2
  test("jailMock works in in the correct way ") {
    assert(jail.getRemainingBlockedMovements(PLAYER_1).isEmpty)
    assert(jail.getRemainingBlockedMovements(PLAYER_2).isEmpty)
    jail.blockPlayer(PLAYER_1, BLOCKING_TIME)
    assert(jail.getRemainingBlockedMovements(PLAYER_1).get == BLOCKING_TIME)
    assert(jail.getRemainingBlockedMovements(PLAYER_2).isEmpty)
    jail.doTurn()
    assert(jail.getRemainingBlockedMovements(PLAYER_1).get == BLOCKING_TIME - 1)
    assert(jail.getRemainingBlockedMovements(PLAYER_2).isEmpty)
    jail.blockPlayer(PLAYER_2, BLOCKING_TIME)
    jail.liberatePlayer(PLAYER_1)
    assert(jail.getRemainingBlockedMovements(PLAYER_1).isEmpty)
    assert(jail.getRemainingBlockedMovements(PLAYER_2).get == BLOCKING_TIME)
    jail.doTurn()
    jail.doTurn()
    jail.doTurn()
    assert(jail.getRemainingBlockedMovements(PLAYER_2).isEmpty)
  }

  val story: EventStory = EventStory(s"You are imprisoned for $BLOCKING_TIME turns", Seq("Wait liberation"))

  test("Behaviour with single Jail event that imprison a player") {
    val imprisonStrategy: Int => Unit = playerId =>
      jail.blockPlayer(playerId, BLOCKING_TIME)
      println("Automatic end of turn") // TODO
    val imprisonEvent = Event(Scenario(imprisonStrategy, None, story))
    val behaviour: Behaviour = Behaviour(Seq(Seq(imprisonEvent)))
    println(behaviour.startBehaviour)
    assertThrows[IllegalArgumentException](behaviour.chooseEvent(PLAYER_1, (1, 0)))
    assertThrows[IllegalArgumentException](behaviour.chooseEvent(PLAYER_1, (0, 1)))
    val nextStories: Seq[StoryGroup] = behaviour.chooseEvent(PLAYER_1, (0, 0))
    assert(nextStories.isEmpty)
  }

  test("On the next turns player must be released from the Jail") {
    val imprisonStrategy: Int => Unit = playerId =>
      jail.getRemainingBlockedMovements(playerId) match
        case None =>
          jail.blockPlayer(playerId, BLOCKING_TIME)
          println("Automatic end of turn") // TODO
        case _ =>

    val imprisonEvent = Event(Scenario(imprisonStrategy, None, story))
    val behaviour: Behaviour = Behaviour(Seq(Seq(imprisonEvent)))
    behaviour.startBehaviour
    assert(jail.getRemainingBlockedMovements(PLAYER_1).isEmpty)
    behaviour.chooseEvent(PLAYER_1, (0, 0))
    assert(jail.getRemainingBlockedMovements(PLAYER_1).get == BLOCKING_TIME)
    jail.doTurn()
    assert(jail.getRemainingBlockedMovements(PLAYER_1).get == BLOCKING_TIME - 1)
    jail.doTurn()
    jail.doTurn()
    assert(jail.getRemainingBlockedMovements(PLAYER_1).isEmpty)
  }
