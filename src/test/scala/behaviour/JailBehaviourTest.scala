package behaviour

import behaviour.BehaviourModule.*
import behaviour.BehaviourModule.Behaviour.*
import behaviour.event.EventModule
import behaviour.event.EventModule.*
import behaviour.event.EventStoryModule.EventStory
import util.mock.JailHelper.JailMock
import org.scalatest.BeforeAndAfterEach
import org.scalatest.funsuite.AnyFunSuite

class JailBehaviourTest extends AnyFunSuite with BeforeAndAfterEach:
  var jail: JailMock = JailMock()
  val PLAYER_1: Int = 1
  val PLAYER_2: Int = 2

  override def beforeEach(): Unit =
    jail = JailMock()

  val BLOCKING_TIME = 2

  val story: EventStory = EventStory(s"You are imprisoned", Seq("Wait liberation"))
  val imprisonEventPredicate: Int => Boolean = _ => true
  val imprisonStrategy: Int => Unit = playerId =>
    jail.getRemainingBlockedMovements(playerId) match
      case None =>
        jail.blockPlayer(playerId, BLOCKING_TIME)
        println("Automatic end of turn") // TODO
      case _ =>
  val imprisonEvent: EventModule.ConditionalEvent =
    Event(Scenario(imprisonStrategy, story), imprisonEventPredicate)

  test("Behaviour with single Jail event that imprison a player") {
    val behaviour: Behaviour = Behaviour(Seq(EventGroup(imprisonEvent)))
    val events = behaviour.getInitialEvents(PLAYER_1)
    assertThrows[IllegalArgumentException](chooseEvent(events)(PLAYER_1, (1, 0)))
    assertThrows[IllegalArgumentException](chooseEvent(events)(PLAYER_1, (0, 1)))
    val nextEvents: Seq[EventGroup] = chooseEvent(events)(PLAYER_1, (0, 0))
    assert(nextEvents.isEmpty)
  }

  test("On the next turns player must be released from the Jail") {

    val behaviour: Behaviour = Behaviour(Seq(EventGroup(imprisonEvent)))
    val events = behaviour.getInitialEvents(PLAYER_1)
    assert(jail.getRemainingBlockedMovements(PLAYER_1).isEmpty)
    chooseEvent(events)(PLAYER_1, (0, 0))
    assert(jail.getRemainingBlockedMovements(PLAYER_1).get == BLOCKING_TIME)
    jail.doTurn()
    assert(jail.getRemainingBlockedMovements(PLAYER_1).get == BLOCKING_TIME - 1)
    jail.doTurn()
    jail.doTurn()
    assert(jail.getRemainingBlockedMovements(PLAYER_1).isEmpty)
  }

  val escapeStrategy: Int => Unit =
    // This version has 100% probability to escape successfully
    jail.liberatePlayer(_)
    // TODO start new movement
  val escapeStory: EventStory = EventStory(s"You have an opportunity to escape", Seq("Try to escape"))
  val escapePrecondition: EventPrecondition = jail.getRemainingBlockedMovements(_).nonEmpty
  val escapeEvent: ConditionalEvent = Event(Scenario(escapeStrategy, escapeStory), escapePrecondition)

  test("Escape event allow to player escape from prison") {
    val behaviour: Behaviour = Behaviour(Seq(EventGroup(imprisonEvent, escapeEvent)))
    var events = behaviour.getInitialEvents(PLAYER_1)
    chooseEvent(events)(PLAYER_1, (0, 0))

    jail.doTurn()
    events = behaviour.getInitialEvents(PLAYER_1)
    assert(jail.getRemainingBlockedMovements(PLAYER_1).nonEmpty)
    chooseEvent(events)(PLAYER_1, (0, 1))
    assert(jail.getRemainingBlockedMovements(PLAYER_1).isEmpty)
  }

  test("Some event may need a precondition before to be available to players") {
    val behaviour: Behaviour = Behaviour(Seq(EventGroup(imprisonEvent, escapeEvent)))
    var events = behaviour.getInitialEvents(PLAYER_1)
    assert(events.length == 1)
    assert(events.head.length == 1)
    chooseEvent(events)(PLAYER_1, (0, 0))
    jail.doTurn()
    events = behaviour.getInitialEvents(PLAYER_1)
    assert(events.length == 1)
    assert(events.head.length == 2)
  }
