package lib.behaviour.factory

import lib.behaviour.BehaviourExplorer
import lib.behaviour.BehaviourModule.Behaviour
import lib.behaviour.event.story.EventStoryModule.EventStory
import lib.behaviour.event.EventFactory
import lib.gameManagement.gameBank.Bank
import lib.util.GameSessionHelper
import org.scalatest.BeforeAndAfterEach
import org.scalatest.funsuite.AnyFunSuite
import GameSessionHelper.DefaultGameSession

/** Test the event that allows to withdraw money of a player
  */
class WithdrawalMoneyEventTest extends AnyFunSuite with BeforeAndAfterEach:
  private val PLAYER_1: Int = 1
  private var bank: Bank = _
  private var behaviour: Behaviour = _
  private val AMOUNT = 100
  private val story: EventStory = EventStory(s"Player lose $AMOUNT money", "Ok")

  override def beforeEach(): Unit =
    val gameSession = DefaultGameSession(1)
    bank = gameSession.gameBank
    behaviour = Behaviour(EventFactory(gameSession).WithdrawMoneyEvent(story, AMOUNT))
    gameSession.startGame()

  test("Withdraw money behaviour must simply withdraw player money") {
    assert(bank.getMoneyOfPlayer(PLAYER_1) == GameSessionHelper.playerInitialMoney)
    val explorer = behaviour.getBehaviourExplorer(PLAYER_1)
    assert(explorer.hasNext)
    explorer.next()
    assert(bank.getMoneyOfPlayer(PLAYER_1) == GameSessionHelper.playerInitialMoney - AMOUNT)
    assert(!explorer.hasNext)
  }
