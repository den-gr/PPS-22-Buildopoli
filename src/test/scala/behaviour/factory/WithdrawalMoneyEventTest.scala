package behaviour.factory

import behaviour.BehaviourIterator
import behaviour.BehaviourModule.{Behaviour, chooseEvent}
import behaviour.event.EventFactory
import behaviour.event.EventStoryModule.EventStory
import gameManagement.gameBank.Bank
import org.scalatest.BeforeAndAfterEach
import org.scalatest.funsuite.AnyFunSuite
import util.GameSessionHelper
import util.GameSessionHelper.DefaultGameSession

class WithdrawalMoneyEventTest extends AnyFunSuite with BeforeAndAfterEach:
  private val PLAYER_1: Int = 1
  private var bank: Bank = _
  private var behaviour: Behaviour = _
  private val AMOUNT = 100
  private val story: EventStory = EventStory(s"Player lose $AMOUNT money", "Ok")

  override def beforeEach(): Unit =
    val gameSession = DefaultGameSession()
    bank = gameSession.gameBank
    behaviour = Behaviour(EventFactory(gameSession).WithdrawMoneyEvent(story, AMOUNT))

  test("Withdraw money behaviour must simply withdraw player money") {
    assert(bank.getMoneyForPlayer(PLAYER_1) == GameSessionHelper.playerInitialMoney)
    val it = behaviour.getBehaviourIterator(PLAYER_1)
    assert(it.hasNext)
    it.next((0, 0))
    assert(bank.getMoneyForPlayer(PLAYER_1) == GameSessionHelper.playerInitialMoney - AMOUNT)
    assert(!it.hasNext)
  }
