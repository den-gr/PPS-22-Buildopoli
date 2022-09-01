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

import behaviour.BehaviourModule.*

class CasinoMockBehaviourTest extends AnyFunSuite with BeforeAndAfterEach:
  private val PLAYER_1: Int = 1

  var bank: BankMock = BankMock()

  override def beforeEach(): Unit =
    bank = BankMock()

  import EventFactory.*
  import EventOperation.*

  private val story = EventStory("You are in casino", "play")
  private val infoEvent = InfoEvent(story, _ => bank.money > 100)

  private val doubleGameStrategy: EventStrategy = id =>
    val opAmount = bank.getPaymentRequestAmount(Player(id), Bank)
    if opAmount.isEmpty then throw new IllegalStateException("Payment to casino not found")
    else if opAmount.get < 100 then throw new IllegalStateException("Payment too low")
    else
      bank.acceptPayment(Player(id), Bank)
      // assume we always lose

  val NUMBER_CHOICES = 5
  private val storyGenerator: StoryGenerator = _ =>
    val desc = "base event description"
    var seq = Seq[String]()
    var interactionSequence = Seq[Interaction]()
    if bank.money <= 100 then EventStory("Not enough money", "Ok")
    else
      for i <- 100 until bank.money by ((bank.money.toDouble / (NUMBER_CHOICES * 100)).ceil * 100).toInt do
        seq = seq :+ i.toString
        val interaction = () => if bank.money > i then OK else ERR("No money")
        interactionSequence = interactionSequence :+ interaction
      EventStory(desc, seq, interactionSequence)

  private val doubleGameEvent = Event(storyGenerator, doubleGameStrategy)

  private val casinoBehaviour = Behaviour(Seq(EventGroup(infoEvent ++ doubleGameEvent)))

  test("Check casino behaviour configuration") {
    val it: BehaviourIterator = casinoBehaviour.getBehaviourIterator(PLAYER_1)
    var events = it.current
    assert(events.length == 1)
    assert(events.head.length == 1)
    assert(events.head.head.eventStory(PLAYER_1).choices.length == 1)
    it.next((0, 0))
    events = it.current
    assert(events.length == 1)
    assert(events.head.length == 1)
    assert(events.head.head.eventStory(PLAYER_1).choices.length == NUMBER_CHOICES)
  }

  test("EventStory of casino must have interactions") {
    var events = casinoBehaviour.getBehaviourIterator(PLAYER_1).current
    val interactions = getStories(events, PLAYER_1)
    assert(interactions.head.head.isInstanceOf[EventStory])
    assert(!interactions.head.head.isInstanceOf[InteractiveEventStory])
    events = chooseEvent(events)(PLAYER_1, (0, 0))
    assert(getStories(events, PLAYER_1).head.head.isInstanceOf[InteractiveEventStory])
  }

  test("In full") {
    //todo
//    var events = casinoBehaviour.getInitialEvents(PLAYER_1)
//    events = chooseEvent(events)(PLAYER_1, (0, 0))
//    val interactions = getStories(events, PLAYER_1)
//    events = chooseEvent(events)(PLAYER_1, (0, 0))
//    assert(events.isEmpty)
  }
