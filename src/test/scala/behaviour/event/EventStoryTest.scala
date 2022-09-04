package behaviour.event

import behaviour.event.EventStoryModule.*
import org.scalatest.BeforeAndAfterEach
import org.scalatest.funsuite.AnyFunSuite
import util.mock.BankHelper.BankMock
import util.mock.BankHelper.BankAccount.*

class EventStoryTest extends AnyFunSuite with BeforeAndAfterEach:
  private val bank = BankMock()
  private val PLAYER_1 = 1

  def createPaymentInteraction(amount: Int): Interaction =
    id =>
      bank.createPaymentRequestAmount(Player(id), Bank, amount); Result.OK

  test("Creation of simple event story without interactions") {
    val story: EventStory = EventStory("Desc", "One")
    assert(!story.isInstanceOf[StoryInteraction])
  }

  private val act1 = createPaymentInteraction(100)
  private val act2 = createPaymentInteraction(200)
  private val choices = Seq("100", "200")

  test("In InteractiveEventStory the number of choices must be equal to the number of interactions") {
    assertThrows[IllegalArgumentException](EventStory("Two choices one interaction", Seq("100", "200"), Seq(act1)))
    assertThrows[IllegalArgumentException](EventStory("One choice two interactions", Seq("100"), Seq(act1, act2)))
    EventStory("How many money do you want send?", choices, Seq(act1, act2))
  }

  test("InteractiveEventStory can generate tuples with choices and interactions") {
    val interactions = Seq(act1, act2)
    val storyInteraction: InteractiveEventStory =
      EventStory("How many money do you want send?", choices, interactions)
    val tuples = storyInteraction.choicesAndInteractions
    assert(tuples.length == choices.length)
    assert(tuples.head._1 == choices.head)
    assert(tuples.head._2 == interactions.head)
    assert(tuples.tail.head._1 == choices.tail.head)
    assert(tuples.tail.head._2 == interactions.tail.head)
  }

  test("Interaction must have effect") {
    val storyInteraction: InteractiveEventStory =
      EventStory("How many money do you want send?", Seq("100", "200"), Seq(act1, act2))
    assert(bank.money == BankMock.BANK_MONEY)
    storyInteraction.choicesAndInteractions.head._2(PLAYER_1)
    assert(bank.getPaymentRequestAmount(Player(PLAYER_1), Bank).nonEmpty)
    bank.acceptPayment(Player(PLAYER_1), Bank)
    assert(bank.money == BankMock.BANK_MONEY - choices.head.toInt)
  }
