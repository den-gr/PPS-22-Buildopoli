package behaviour

import org.scalatest.BeforeAndAfterEach
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.BeforeAndAfterEach
import org.scalatest.funsuite.AnyFunSuite
import util.mock.BankHelper.BankMock
import util.mock.BankHelper.BankAccount.*
import behaviour.BehaviourModule.*
import behaviour.event.*
import behaviour.event.EventModule.*
import behaviour.event.EventStoryModule.*
import behaviour.event.EventStoryModule.Result.*
import EventFactory.*
import EventOperation.*

import behaviour.BehaviourModule.*

/** Test Behaviour by using a casino terrain behaviour. Game bank is replaces by a mock
  */
class CasinoMockBehaviourTest extends AnyFunSuite with BeforeAndAfterEach:
  private val PLAYER_1: Int = 1

  var bank: BankMock = BankMock()

  override def beforeEach(): Unit =
    bank = BankMock()

  private val story = EventStory("You are in casino", "play")
  private val infoEvent = InfoEvent(story, _ => bank.money > 100)

  private val loseGameStrategy: EventStrategy = id =>
    bank.getPaymentRequestAmount(Player(id), Bank) match
      case None => throw new IllegalStateException("Payment to casino not found")
      case Some(n) if n > 0 => bank.acceptPayment(Player(id), Bank) // assume we always lose

  val WIN_MULTIPLAYER = 10
  private val winGameStrategy: EventStrategy = id =>
    bank.getPaymentRequestAmount(Player(id), Bank) match
      case None => throw new IllegalStateException("Payment to casino not found")
      case Some(n) if n > 0 =>
        bank.acceptPayment(Player(id), Bank)
        bank.money = n * WIN_MULTIPLAYER

  val NUMBER_CHOICES = 5
  private val storyGenerator: StoryGenerator = _ =>
    val desc = "base event description"
    var seq = Seq[String]()
    var interactionSequence = Seq[Interaction]()
    if bank.money <= 100 then EventStory("Not enough money for casino", "Ok")
    else
      for i <- 100 until bank.money by ((bank.money.toDouble / (NUMBER_CHOICES * 100)).ceil * 100).toInt do
        seq = seq :+ i.toString
        val interaction: Interaction = _ => if bank.money > i then OK else ERR("No money")
        interactionSequence = interactionSequence :+ interaction
      EventStory(desc, seq, interactionSequence)

  private val loseGameEvent = Event(storyGenerator, loseGameStrategy)
  private val winGameEvent = Event(storyGenerator, winGameStrategy)

  private val ATOMIC = true

  private val eventGroups =
    Seq(EventGroup(infoEvent ++ loseGameEvent), EventGroup(Seq(infoEvent ++ winGameEvent), ATOMIC))
  private val casinoBehaviour = Behaviour(eventGroups)

  test("Check casino behaviour event groups") {
    val events = casinoBehaviour.getBehaviourIterator(PLAYER_1).currentEvents
    assert(events.length == 2)
    assert(events.head.length == 1)
    assert(events.head.head.eventStory(PLAYER_1).choices.length == 1)
  }

  test("Check casino behaviour lose game event") {
    val it: BehaviourIterator = casinoBehaviour.getBehaviourIterator(PLAYER_1)
    it.next()
    val events = it.currentEvents
    assert(events.length == 2)
    assert(events.head.length == 1)
    assert(events.head.head.eventStory(PLAYER_1).choices.length == NUMBER_CHOICES)
  }

  ignore("Check casino behaviour win game event") {
    val it: BehaviourIterator = casinoBehaviour.getBehaviourIterator(PLAYER_1)
    it.next((1, 0))
    val events = it.currentEvents
    assert(events.length == 1) // second event group is ignored
    assert(events.head.length == 1)
    println(events.head.head.eventStory(PLAYER_1).choices)
    assert(events.head.head.eventStory(PLAYER_1).choices.length == NUMBER_CHOICES)
  }

  test("EventStory of casino must have interactions") {
    val it: BehaviourIterator = casinoBehaviour.getBehaviourIterator(PLAYER_1)
    val events: Seq[EventGroup] = it.currentEvents
    val interactions: Seq[StoryGroup] = getStories(events, PLAYER_1)
    assert(interactions.head.head.isInstanceOf[EventStory])
    assert(!interactions.head.head.isInstanceOf[InteractiveEventStory])
    it.next()
    assert(getStories(it.currentEvents, PLAYER_1).head.head.isInstanceOf[InteractiveEventStory])
  }
