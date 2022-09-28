package buildopoli.mock

import org.scalatest.BeforeAndAfterEach
import org.scalatest.funsuite.AnyFunSuite
import buildopoli.util.mock.BankHelper.BankMock
import buildopoli.util.mock.BankHelper.BankMock.*
import buildopoli.util.mock.BankHelper.BankAccount.*
import buildopoli.util.mock.JailHelper.JailMock

class TestMock extends AnyFunSuite with BeforeAndAfterEach:
  val PLAYER_ID = 1
  var bank: BankMock = BankMock()

  override def beforeEach(): Unit =
    bank = BankMock()

  test("Decrement bank money") {
    assert(bank.money == BANK_MONEY)
    bank.decrement(TAX)
    assert(bank.money == BANK_MONEY - TAX)
    bank.decrement(TAX)
    assert(bank.money == BANK_MONEY - TAX * 2)
  }

  test("Set new bank money"){
    assert(bank.money == BANK_MONEY)
    bank.money = BANK_MONEY - 100
    assert(bank.money == BANK_MONEY - 100)
  }

  test("Make and check payment"){
    val amount = 100
    assert(bank.getPaymentRequestAmount(Player(PLAYER_ID), Bank).isEmpty)
    bank.createPaymentRequestAmount(Player(PLAYER_ID), Bank, amount)
    val request = bank.getPaymentRequestAmount(Player(PLAYER_ID), Bank)
    assert(request.nonEmpty)
    assert(request.get == amount)
    bank.acceptPayment(Player(PLAYER_ID), Bank)
    assert(bank.getPaymentRequestAmount(Player(PLAYER_ID), Bank).isEmpty)
  }

  var jail: JailMock = JailMock()
  val PLAYER_1: Int = 1
  val PLAYER_2: Int = 2

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


