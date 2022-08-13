package behaviour

import behaviour.BehaviourModule.{Behaviour, StoryGroup}
import events.EventModule
import events.EventModule.*
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

  val story: EventStory = EventStory(s"You are imprisoned", Seq("Wait liberation"))
  val imprisonEventPredicate: Int => Boolean = _ => true
  val imprisonStrategy: Int => Unit = playerId =>
    jail.getRemainingBlockedMovements(playerId) match
      case None =>
        jail.blockPlayer(playerId, BLOCKING_TIME)
        println("Automatic end of turn") // TODO
      case _ =>
  val imprisonEvent: EventModule.ConditionalEvent =
    Event(Scenario(imprisonStrategy, None, story), imprisonEventPredicate)

  test("Behaviour with single Jail event that imprison a player") {
    val behaviour: Behaviour = Behaviour(Seq(Seq(imprisonEvent)))
    println(behaviour.startBehaviour(PLAYER_1))
    assertThrows[IllegalArgumentException](behaviour.chooseEvent(PLAYER_1, (1, 0)))
    assertThrows[IllegalArgumentException](behaviour.chooseEvent(PLAYER_1, (0, 1)))
    val nextStories: Seq[StoryGroup] = behaviour.chooseEvent(PLAYER_1, (0, 0))
    assert(nextStories.isEmpty)
  }

  test("On the next turns player must be released from the Jail") {

    val behaviour: Behaviour = Behaviour(Seq(Seq(imprisonEvent)))
    behaviour.startBehaviour(PLAYER_1)
    assert(jail.getRemainingBlockedMovements(PLAYER_1).isEmpty)
    behaviour.chooseEvent(PLAYER_1, (0, 0))
    assert(jail.getRemainingBlockedMovements(PLAYER_1).get == BLOCKING_TIME)
    jail.doTurn()
    assert(jail.getRemainingBlockedMovements(PLAYER_1).get == BLOCKING_TIME - 1)
    jail.doTurn()
    jail.doTurn()
    assert(jail.getRemainingBlockedMovements(PLAYER_1).isEmpty)
  }

  test("Some event may have precondition for to be available to player") {
    val escapeStrategy: Int => Unit =
      // This version has 100% probability to escape successfully
      jail.liberatePlayer(_)
      // TODO start new movement
    val escapeStory = EventStory(s"You have an opportunity to escape", Seq("Try to escape"))
    val escapePrecondition: EventPrecondition = jail.getRemainingBlockedMovements(_).nonEmpty
    val escapeEvent = Event(Scenario(escapeStrategy, None, escapeStory), escapePrecondition)

    val behaviour: Behaviour = Behaviour(Seq(Seq(imprisonEvent, escapeEvent)))
    var stories = behaviour.startBehaviour(PLAYER_1)
    println(stories)
    assert(stories.length == 1)
    assert(stories.head.length == 1)
    behaviour.chooseEvent(PLAYER_1, (0, 0))
    jail.doTurn()
    stories = behaviour.startBehaviour(PLAYER_1)
    println(stories)
    assert(stories.length == 1)
    assert(stories.head.length == 2)

  }
