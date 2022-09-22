package lib.integration

import lib.behaviour.BehaviourExplorer
import lib.behaviour.BehaviourModule.Behaviour
import lib.behaviour.event.story.EventStoryModule.EventStory
import lib.behaviour.event.EventFactory
import lib.gameManagement.gameBank.Bank
import lib.util.GameSessionHelper
import org.scalatest.{BeforeAndAfterEach, GivenWhenThen}
import org.scalatest.funsuite.AnyFunSuite
import GameSessionHelper.DefaultGameSession
import lib.behaviour.factory.BehaviourFactory
import lib.terrain.{Terrain, TerrainInfo}
import org.scalatest.featurespec.AnyFeatureSpec

class WithdrawMoneyTerrainTest extends AnyFeatureSpec with BeforeAndAfterEach:
  private val PLAYER_1: Int = 1
  private val AMOUNT = 100
  private val AMOUNT2 = 50
  private val story: EventStory = EventStory(s"Player lose money", "Ok")
  private var gameSession = DefaultGameSession(1)
  private var bank: Bank = gameSession.gameBank

  override def beforeEach(): Unit =
    gameSession = DefaultGameSession(1)
    bank = gameSession.gameBank
    val eventFactory = EventFactory(gameSession)
    val behaviour: Behaviour = Behaviour(eventFactory.WithdrawMoneyEvent(story, AMOUNT))
    val behaviour2: Behaviour = Behaviour(eventFactory.WithdrawMoneyEvent(story, AMOUNT2))

    gameSession.gameStore.putTerrain(Terrain(TerrainInfo(s"Loosing $AMOUNT money"), behaviour))
    gameSession.gameStore.putTerrain(Terrain(TerrainInfo(s"Loosing $AMOUNT2 money"), behaviour2))

    gameSession.startGame()

  Feature("Player arrived in the terrain with a behaviour that withdraw player money") {
    Scenario("Use behaviour of a terrain where player is located") {
      assert(gameSession.getPlayerPosition(PLAYER_1) == 0)
      val explorer = gameSession.getFreshBehaviourExplorer(PLAYER_1)
      testAmount(explorer, AMOUNT)
    }

    Scenario("Player moves to a second terrain and its behaviour withdraws another amount of money") {
      gameSession.movePlayer(PLAYER_1, steps = 1)
      val explorer = gameSession.getFreshBehaviourExplorer(PLAYER_1)
      testAmount(explorer, AMOUNT2)
    }
  }

  private def testAmount(explorer: BehaviourExplorer, amount: Int): Unit =
    assert(bank.getMoneyForPlayer(PLAYER_1) == GameSessionHelper.playerInitialMoney)
    explorer.next()
    assert(bank.getMoneyForPlayer(PLAYER_1) == GameSessionHelper.playerInitialMoney - amount)
    assert(!explorer.hasNext)
