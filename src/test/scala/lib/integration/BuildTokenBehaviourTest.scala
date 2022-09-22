package lib.integration

import lib.behaviour.BehaviourExplorer
import lib.behaviour.BehaviourModule.Behaviour
import lib.behaviour.event.EventModule.Event
import lib.behaviour.event.{EventFactory, EventGroup}
import lib.behaviour.event.story.EventStoryModule.{EventStory, StoryGroup}
import lib.behaviour.factory.BehaviourFactory
import lib.gameManagement.gameSession.GameSession
import org.scalatest.{BeforeAndAfterEach, GivenWhenThen}
import lib.terrain.Mortgage.DividePriceMortgage
import lib.terrain.RentStrategy.BasicRentStrategyFactor
import lib.terrain.{Buildable, Purchasable, Terrain, TerrainInfo, Token}
import lib.util.GameSessionHelper.DefaultGameSession
import org.scalatest.featurespec.AnyFeatureSpec
import lib.behaviour.event.story.InteractiveEventStoryModule.{Interaction, InteractiveEventStory, Result}
import lib.util.GameSessionHelper

/**
 * Test building of tokens (buildings) on a buildable terrain. Behaviour is created by [[BehaviourFactory]] 
 */
class BuildTokenBehaviourTest extends AnyFeatureSpec with GivenWhenThen with BeforeAndAfterEach:
  private val PLAYER_1 = 1

  private var globalBuildableEvent: Event = _
  private val TERRAIN_PRICE = 50
  private val RENT = 50
  private val ROMA = "Roma"
  private val MILANO = "Milano"
  private val HOUSE = "House"
  private val HOTEL = "HOTEL"

  private var gameSession: GameSession = _

  override def beforeEach(): Unit =
    gameSession = DefaultGameSession(2)
    val factory = EventFactory(gameSession)

    globalBuildableEvent = factory.BuildTokenEvent(
      "Terrain where you can build",
      "Select type of building",
      "Select number of building",
      "Not enough money for"
    )

    val buyTerrainStory: EventStory = EventStory("Do you want but this terrain?", "Buy")
    val rentStory: EventStory = EventStory("You need to pay rent", "Pay")
    val behaviourFactory = BehaviourFactory(gameSession)
    val PurchasableBehaviour: Behaviour =
      behaviourFactory.PurchasableTerrainBehaviour(rentStory, "Not enough money", buyTerrainStory)

    val t: Terrain = Terrain(TerrainInfo(ROMA), PurchasableBehaviour)
    val t2: Terrain = Terrain(TerrainInfo(MILANO), PurchasableBehaviour)
    val purchasableTerrain =
      Purchasable(t, TERRAIN_PRICE, "fucsia", DividePriceMortgage(1000, 3), BasicRentStrategyFactor(RENT, 1))
    val purchasableTerrain2 =
      Purchasable(t2, TERRAIN_PRICE, "fucsia2", DividePriceMortgage(1000, 3), BasicRentStrategyFactor(RENT, 1))

    val token = Token(Seq(HOUSE, HOTEL), Seq(2, 1), Seq(Seq(20, 30), Seq(100)), Seq(100, 50))
    gameSession.gameStore.putTerrain(Buildable(purchasableTerrain, token))
    gameSession.gameStore.putTerrain(Buildable(purchasableTerrain2, token))
    gameSession.gameStore.globalBehaviour = Behaviour(globalBuildableEvent)
    gameSession.startGame()

  def getFreshExplorer: BehaviourExplorer = gameSession.getFreshBehaviourExplorer(PLAYER_1)

  Feature("Buildable terrain allows to players to build tokens (building)") {
    Scenario("When terrain is not bought player do not see build token event") {
      Given("combination of global and terrain behaviours")
      var explorer = getFreshExplorer
      assert(explorer.currentEvents.length == 1)

      When("player buy the terrain")
      explorer.next()
      assert(!explorer.hasNext)

      Then("player start to see the global event for token building")
      explorer = getFreshExplorer
      assert(explorer.currentEvents.head.head == globalBuildableEvent)
    }

    Scenario("Player buy two terrains from different groups") {
      Given("player 1 start its turn")
      var explorer = getFreshExplorer

      When("player buy two terrains")
      explorer.next()
      gameSession.movePlayer(explorer.playerId, steps = 1)
      explorer = getFreshExplorer
      explorer.next()

      Then("player is can choose in which terrain build a house")
      explorer = getFreshExplorer
      assert(explorer.currentStories.head.head.choices.length == 2)
      assert(explorer.currentStories.head.head.choices.head == ROMA)
      assert(explorer.currentStories.head.head.choices.last == MILANO)
    }

    Scenario("Player can build a house") {
      Given("player bought the terrain")
      var explorer = getFreshExplorer
      explorer.next()

      When("player select the terrain where he wants to build a building")
      explorer = getFreshExplorer
      assert(getInteractions(explorer).head(explorer.playerId) == Result.OK)
      explorer.next()

      Then(s"player see only $HOUSE as a choice")
      assert(explorer.currentStories.head.head.choices.head == HOUSE)

      Then(s"player select this $HOUSE")
      assert(getInteractions(explorer).head(explorer.playerId) == Result.OK)
      explorer.next()

      When("player select ot build 2 houses")
      Then("error message appears")
      assert(
        getInteractions(explorer)(1)(explorer.playerId) match
          case Result.ERR(_) => true
          case _ => false
      )

      When("player select to build 1 house")
      assert(getInteractions(explorer).head(explorer.playerId) == Result.OK)
      explorer.next()

      Then("player become owner of terrain")
      assert(gameSession.getPlayerTerrain(explorer.playerId).asInstanceOf[Buildable].owner.get == explorer.playerId)
    }

    Scenario("Player cen not proceed to token choosing when he have not money to build") {
      Given("player bought the terrain")
      var explorer = getFreshExplorer
      explorer.next()

      When("player have not money")
      gameSession.gameBank.makeTransaction(explorer.playerId, amount = GameSessionHelper.playerInitialMoney)

      Then("Player can not proceed with terrain selection")
      explorer = getFreshExplorer
      assert(getInteractions(explorer).head(explorer.playerId) match
        case Result.ERR(_) => true
        case _ => false
      )
    }
  }

  private def getInteractions(explorer: BehaviourExplorer): Seq[Interaction] =
    explorer.currentStories.head.head.asInstanceOf[InteractiveEventStory].interactions
