package events

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.*
import EventModule.*
import helper.TestMocks.*
import helper.TestMocks.BankMock.*

class EventTest extends AnyFunSuite with BeforeAndAfterEach:
  val MOCK_ID: Int = 0
  var bank: BankMock = BankMock()
  override def beforeEach(): Unit =
    bank = BankMock()

  test("test fake Bank") {
    assert(bank.money == BANK_MONEY)
    bank.decrement(TAX)
    assert(bank.money == BANK_MONEY - TAX)
    bank.decrement(TAX)
    assert(bank.money == BANK_MONEY - TAX * 2)
  }

  val eventStrategy: EventStrategy = _ => bank.decrement(TAX)
  test("test decrement from strategy") {
    assert(bank.money == BANK_MONEY)
    eventStrategy(MOCK_ID)
    assert(bank.money == BANK_MONEY - TAX)
    eventStrategy(MOCK_ID)
    assert(bank.money == BANK_MONEY - TAX * 2)
  }

  import EventModule.*
  import EventStory.*
  val ev: Event = Event(Scenario(eventStrategy, None), _ => true)
  test("I want an event that can change balance of a bank") {
    ev.run(MAIN_ACTION)
    assert(bank.money == BANK_MONEY - TAX)
    ev.run(MAIN_ACTION)
    assert(bank.money == BANK_MONEY - TAX * 2)
  }

  test("I want to have two consecutive events") {
    assert(bank.money == BANK_MONEY)
    val ev2: Event = Event(Scenario(_ => bank.decrement(TAX * 2), Some(ev)), _ => true)
    var nextEvent = ev2.run(MAIN_ACTION)
    while nextEvent.nonEmpty do
      if nextEvent.get.eventStory.isSingleAction then nextEvent = nextEvent.get.run(MAIN_ACTION)
      else fail("Multiple action case is not supported now")
    assert(bank.money == BANK_MONEY - TAX * 3)
  }
