package lib.integration

import lib.behaviour.BehaviourExplorer
import lib.behaviour.BehaviourModule.Behaviour
import lib.behaviour.event.EventFactory
import lib.behaviour.event.EventModule.Event
import lib.behaviour.event.story.EventStoryModule.EventStory
import lib.behaviour.event.story.InteractiveEventStoryModule.{InteractiveEventStory, Result}
import lib.behaviour.factory.BehaviourFactory
import lib.gameManagement.gameSession.GameSession
import lib.terrain.Mortgage.*
import lib.terrain.RentStrategy.BasicRentStrategyFactor
import lib.terrain.{Buildable, Purchasable, Terrain, TerrainInfo, Token}
import lib.util.GameSessionHelper
import lib.util.GameSessionHelper.DefaultGameSession
import org.scalatest.{BeforeAndAfterEach, GivenWhenThen}
import org.scalatest.featurespec.AnyFeatureSpec

import scala.collection.immutable.Seq

class MortgageBehaviourTest extends AnyFeatureSpec with GivenWhenThen with BeforeAndAfterEach:
  private val PLAYER_1 = 1
  private var globalBehaviour: Behaviour = _
  private var globalBuildableEvent: Event = _
  private val TERRAIN_PRICE = 50
  private val RENT = 50

  private var gameSession: GameSession = _

  override def beforeEach(): Unit =
    gameSession = DefaultGameSession(2)
    val factory = EventFactory(gameSession)

    globalBuildableEvent = factory.MortgageEvent("You can mortgage your terrain and receive money")
    globalBehaviour = Behaviour(globalBuildableEvent)

    val buyTerrainStory: EventStory = EventStory("Do you want but this terrain?", "Buy")
    val rentStory: EventStory = EventStory("You need to pay rent", "Pay")
    val behaviourFactory = BehaviourFactory(gameSession)
    val PurchasableBehaviour: Behaviour =
      behaviourFactory.PurchasableTerrainBehaviour(rentStory, "Not enough money", buyTerrainStory)

    val t: Terrain = Terrain(TerrainInfo("Nome1"), PurchasableBehaviour)
    val t2: Terrain = Terrain(TerrainInfo("NOme2"), PurchasableBehaviour)
    val purchasableTerrain =
      Purchasable(t, TERRAIN_PRICE, "fucsia", DividePriceMortgage(1000, 3), BasicRentStrategyFactor(RENT, 1))
    val purchasableTerrain2 =
      Purchasable(t2, TERRAIN_PRICE, "fucsia2", DividePriceMortgage(1000, 3), BasicRentStrategyFactor(RENT, 1))

    val token = Token(Seq("house"), Seq(4), Seq(Seq(20, 20, 20, 20)), Seq(25))
    gameSession.gameStore.putTerrain(Buildable(purchasableTerrain, token))
    gameSession.gameStore.putTerrain(purchasableTerrain2)
    gameSession.startGame()

  def getFreshExplorer: BehaviourExplorer =
    Behaviour.combineExplorers(gameSession.getPlayerTerrain(PLAYER_1).behaviour, globalBehaviour, PLAYER_1)

  Feature("Player is able to mortgage his terrains for receive money") {
    Scenario("When terrain is not bought player do not see build mortgage event") {
      Given("combination of global and terrain behaviours")
      var explorer = getFreshExplorer
      assert(explorer.currentEvents.length == 1)

      When("player buy the terrain")
      explorer.next()
      assert(!explorer.hasNext)

      Then("player start to see the global event for mortgage event")
      explorer = getFreshExplorer
      assert(explorer.currentEvents.head.head == globalBuildableEvent)
    }

    Scenario("Player buy a terrain and mortgage it") {
      Given("player 1 start its turn")
      var explorer = getFreshExplorer

      When("player buy first terrain")
      assert(
        explorer.currentStories.head.head
          .asInstanceOf[InteractiveEventStory]
          .interactions
          .head(explorer.playerId) == Result.OK
      )
      explorer.next()

      Then("player mortgage this terrain")
      explorer = getFreshExplorer
      assert(explorer.currentStories.head.head.choices.length == 1)
      assert(
        explorer.currentStories.head.head
          .asInstanceOf[InteractiveEventStory]
          .interactions
          .head(explorer.playerId) == Result.OK
      )
      explorer.next()
      assert(gameSession.gameBank.getMoneyForPlayer(explorer.playerId) > GameSessionHelper.playerInitialMoney)
    }
  }
