package buildopoli.behaviour

import buildopoli.behaviour.BehaviourExplorer
import buildopoli.behaviour.BehaviourModule.*
import buildopoli.behaviour.BehaviourModule.Behaviour.*
import buildopoli.behaviour.event.EventModule.*
import buildopoli.behaviour.event.story.EventStoryModule.EventStory
import buildopoli.behaviour.event.{EventGroup, EventModule}
import buildopoli.util.mock.JailHelper.JailMock
import org.scalatest.BeforeAndAfterEach
import org.scalatest.funsuite.AnyFunSuite

/** Test Behaviour by using a jail terrain behaviour. Jail is replaces by a mock
  */
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
    var explorer = Behaviour(imprisonEvent).getBehaviourExplorer(PLAYER_1)
    assertThrows[IllegalArgumentException](explorer.next((1, 0)))
    assertThrows[IllegalArgumentException](explorer.next((0, 1)))

    explorer = Behaviour(imprisonEvent).getBehaviourExplorer(PLAYER_1)
    explorer = explorer.next()
    assert(!explorer.hasNext)
  }

  test("If event group is mandatory explorer is not possible to end exploring behaviour") {
    val explorer = Behaviour(EventGroup(Seq(imprisonEvent), isMandatory = true)).getBehaviourExplorer(PLAYER_1)
    assert(explorer.hasNext)
    assert(!explorer.canEndExploring)
  }

  test("If event group is not mandatory a player can skip all events") {
    var explorer = Behaviour(EventGroup(Seq(imprisonEvent))).getBehaviourExplorer(PLAYER_1)
    assert(explorer.hasNext)
    assert(explorer.canEndExploring)
    explorer = explorer.endExploring()
    assert(!explorer.hasNext)
  }

  test("On the next turns player must be released from the Jail") {

    val behaviour: Behaviour = Behaviour(imprisonEvent)
    val explorer = behaviour.getBehaviourExplorer(PLAYER_1)
    assert(jail.getRemainingBlockedMovements(PLAYER_1).isEmpty)
    explorer.next((0, 0))
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
    val behaviour: Behaviour = Behaviour(imprisonEvent, escapeEvent)
    var explorer = behaviour.getBehaviourExplorer(PLAYER_1)
    explorer.next((0, 0))

    jail.doTurn()
    explorer = behaviour.getBehaviourExplorer(PLAYER_1)
    assert(jail.getRemainingBlockedMovements(PLAYER_1).nonEmpty)
    explorer.next((0, 1))
    assert(jail.getRemainingBlockedMovements(PLAYER_1).isEmpty)
  }

  test("Some event may need a precondition before to be available to players") {
    val behaviour: Behaviour = Behaviour(Seq(EventGroup(imprisonEvent, escapeEvent)))
    val explorer = behaviour.getBehaviourExplorer(PLAYER_1)
    var events = explorer.currentEvents
    assert(events.length == 1)
    assert(events.head.length == 1)
    explorer.next((0, 0))
    jail.doTurn()
    events = behaviour.getBehaviourExplorer(PLAYER_1).currentEvents
    assert(events.length == 1)
    assert(events.head.length == 2)
  }

  test("To a behaviour is possible to add new events") {
    val behaviour = Behaviour(Seq(EventGroup(imprisonEvent)))
    assert(behaviour.getBehaviourExplorer(PLAYER_1).currentEvents.length == 1)
    val newGroup = EventGroup(Event(EventStory("dfdff", "")))
    val newIt = behaviour.addEventGroups(Seq(newGroup)).getBehaviourExplorer(PLAYER_1)
    assert(newIt.currentEvents.length == 2)
    assert(newIt.currentEvents.last == newGroup)
  }
