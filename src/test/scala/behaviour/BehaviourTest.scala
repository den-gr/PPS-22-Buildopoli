package behaviour

import org.scalatest.funsuite.AnyFunSuite
import BehaviourModule.*
import events.EventModule.*
import org.scalatest.*
import helper.TestMocks.*
import helper.TestMocks.BankMock.*

class BehaviourTest extends AnyFunSuite with BeforeAndAfterEach:

  var bank: BankMock = BankMock()

  override def beforeEach(): Unit =
    bank = BankMock()

  import Scenario.*
  val eventStrategy: () => Unit = () => bank.decrement(TAX)
