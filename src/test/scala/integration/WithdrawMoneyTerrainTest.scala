package integration

import behaviour.BehaviourIterator
import behaviour.BehaviourModule.Behaviour
import behaviour.event.EventFactory
import behaviour.event.EventStoryModule.EventStory
import gameManagement.gameBank.Bank
import org.scalatest.BeforeAndAfterEach
import org.scalatest.funsuite.AnyFunSuite
import terrain.{Terrain, TerrainInfo}
import util.GameSessionHelper
import util.GameSessionHelper.DefaultGameSession

class WithdrawMoneyTerrainTest extends AnyFunSuite with BeforeAndAfterEach:
  private val PLAYER_1: Int = 1
  private val AMOUNT = 100
  private val AMOUNT2 = 50
  private val story: EventStory = EventStory(s"Player lose money", "Ok")
  private var gameSession = DefaultGameSession(1)
  private var bank: Bank = gameSession.gameBank

  override def beforeEach(): Unit =
    gameSession = DefaultGameSession(1)
    bank = gameSession.gameBank
    val behaviour: Behaviour = Behaviour(EventFactory(gameSession).WithdrawMoneyEvent(story, AMOUNT))
    val behaviour2: Behaviour = Behaviour(EventFactory(gameSession).WithdrawMoneyEvent(story, AMOUNT2))

    gameSession.gameStore.putTerrain(Terrain(TerrainInfo(s"Loosing $AMOUNT money", 0, behaviour)))
    gameSession.gameStore.putTerrain(Terrain(TerrainInfo(s"Loosing $AMOUNT2 money", 1, behaviour2)))

  test("Use behaviour of a terrain where player is located") {
    assert(gameSession.getPlayerPosition(PLAYER_1) == 0)
    val it = gameSession.getPlayerTerrain(PLAYER_1).basicInfo.behaviour.getBehaviourIterator(PLAYER_1)
    testAmount(it, AMOUNT)
  }

  test("When player moves it arrives in new terrain with new behaviour that withdraw another amount of money") {
    gameSession.setPlayerPosition(PLAYER_1, 1, true)
    val it = gameSession.getPlayerTerrain(PLAYER_1).basicInfo.behaviour.getBehaviourIterator(PLAYER_1)
    testAmount(it, AMOUNT2)
  }

  private def testAmount(it: BehaviourIterator, amount: Int): Unit =
    assert(bank.getMoneyForPlayer(PLAYER_1) == GameSessionHelper.playerInitialMoney)
    it.next()
    assert(bank.getMoneyForPlayer(PLAYER_1) == GameSessionHelper.playerInitialMoney - amount)
    assert(!it.hasNext)
