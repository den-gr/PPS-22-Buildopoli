package behaviour

import behaviour.BehaviourModule.*
import behaviour.BehaviourModule.Behaviour.*
import behaviour.event.EventModule
import behaviour.event.EventModule.*
import behaviour.event.EventGroup
import behaviour.event.EventStoryModule.EventStory
import util.mock.JailHelper.JailMock
import org.scalatest.BeforeAndAfterEach
import org.scalatest.funsuite.AnyFunSuite

class JailMockBehaviourTest extends AnyFunSuite with BeforeAndAfterEach:
  var jail: JailMock = _
  val PLAYER_1: Int = 1
  val PLAYER_2: Int = 2

  override def beforeEach(): Unit =
    jail = JailMock()

  val BLOCKING_TIME = 2

  val story: EventStory = EventStory(s"You are imprisoned", "Wait liberation")
  val imprisonStrategy: Int => Unit = playerId =>
    jail.getRemainingBlockedMovements(playerId) match
      case None =>
        jail.blockPlayer(playerId, BLOCKING_TIME)
        println("Automatic end of turn") // TODO
      case _ =>
  val imprisonEvent: Event =
    Event(story, imprisonStrategy)

  test("Behaviour with single Jail event that imprison a player") {
    var it = Behaviour(Seq(EventGroup(imprisonEvent))).getBehaviourIterator(PLAYER_1)
    assertThrows[IllegalArgumentException](it.next((1, 0)))
    assertThrows[IllegalArgumentException](it.next((0, 1)))

    it = Behaviour(Seq(EventGroup(imprisonEvent))).getBehaviourIterator(PLAYER_1)
    it.next((0, 0))
    assert(!it.hasNext)
  }

  test("On the next turns player must be released from the Jail") {

    val behaviour: Behaviour = Behaviour(imprisonEvent)
    val it = behaviour.getBehaviourIterator(PLAYER_1)
    assert(jail.getRemainingBlockedMovements(PLAYER_1).isEmpty)
    it.next((0, 0))
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
  val escapeStory: EventStory = EventStory(s"You have an opportunity to escape", "Try to escape")
  val escapePrecondition: EventPrecondition = jail.getRemainingBlockedMovements(_).nonEmpty
  val escapeEvent: Event = Event(escapeStory, escapeStrategy, escapePrecondition)

  test("Escape event allow to player escape from prison") {
    val behaviour: Behaviour = Behaviour(Seq(EventGroup(imprisonEvent, escapeEvent)))
    var it = behaviour.getBehaviourIterator(PLAYER_1)
    it.next((0, 0))

    jail.doTurn()
    it = behaviour.getBehaviourIterator(PLAYER_1)
    assert(jail.getRemainingBlockedMovements(PLAYER_1).nonEmpty)
    it.next((0, 1))
    assert(jail.getRemainingBlockedMovements(PLAYER_1).isEmpty)
  }

  test("Some event may need a precondition before to be available to players") {
    val behaviour: Behaviour = Behaviour(Seq(EventGroup(imprisonEvent, escapeEvent)))
    val it = behaviour.getBehaviourIterator(PLAYER_1)
    var events = it.current
    assert(events.length == 1)
    assert(events.head.length == 1)
    chooseEvent(events)(PLAYER_1, (0, 0))
    jail.doTurn()
    events = behaviour.getBehaviourIterator(PLAYER_1).current
    assert(events.length == 1)
    assert(events.head.length == 2)
  }
