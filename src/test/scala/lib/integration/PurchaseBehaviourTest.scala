package lib.integration

import org.scalatest.{BeforeAndAfterEach, GivenWhenThen}
import org.scalatest.funsuite.AnyFunSuite
import lib.terrain.Mortgage.DividePriceMortgage
import lib.terrain.RentStrategy.{BasicRentStrategyFactor, RentStrategyWithBonus}
import lib.behaviour.BehaviourModule.*
import lib.behaviour.event.story.EventStoryModule.EventStory
import lib.gameManagement.gameSession.GameSession
import lib.util.GameSessionHelper
import org.scalatest.featurespec.AnyFeatureSpec
import GameSessionHelper.DefaultGameSession
import lib.behaviour.event.story.InteractiveEventStoryModule.*
import lib.behaviour.event.{EventFactory, EventGroup}
import lib.behaviour.factory.BehaviourFactory
import lib.terrain.{GroupManager, Purchasable, Terrain, TerrainInfo}

/** Test purchasable events produced by [[BehaviourFactory]] that allow buying terrains and pay rent to the owner
  */
class PurchaseBehaviourTest extends AnyFeatureSpec with GivenWhenThen with BeforeAndAfterEach:
  private val PLAYER_1 = 1
  private val PLAYER_2 = 2
  private var simpleTerrain: Terrain = _
  private var purchasableTerrain: Purchasable = _
  private val TERRAIN_PRICE = 100
  private val RENT = 50
  val buyTerrainStory: EventStory = EventStory("Do you want but this terrain?", "Buy")
  val rentStory: EventStory = EventStory("You need to pay rent", "Pay")

  private var gameSession: GameSession = _
  override def beforeEach(): Unit =
    gameSession = DefaultGameSession(2)

    val factory = BehaviourFactory(gameSession)
    val behaviour: Behaviour = factory.PurchasableTerrainBehaviour(rentStory, "Not enough money", buyTerrainStory)

    simpleTerrain = Terrain(TerrainInfo("Dumb terrain"), behaviour)
    val t: Terrain = Terrain(TerrainInfo("vicolo corto"), behaviour)
    purchasableTerrain =
      Purchasable(t, TERRAIN_PRICE, "fucsia", DividePriceMortgage(1000, 3), BasicRentStrategyFactor(RENT, 1))
    gameSession.gameStore.putTerrain(purchasableTerrain)
    gameSession.gameStore.putTerrain(simpleTerrain)
    gameSession.startGame()

  info("Purchasable Terrain can be bought by a player, in this case another players must pay rent")

  Feature("Purchasable Terrain can be bought by a player") {
    Scenario("Buy terrain event is compatible only with Purchasable terrains") {
      When("a player on a simple Terrain with purchasable event")
      gameSession.movePlayer(PLAYER_1, steps = 1)

      Then("we have illegal state exception")
      assertThrows[IllegalStateException](gameSession.getFreshBehaviourExplorer(PLAYER_1))
    }

    Scenario("Terrain interaction return different answer based on money of the player") {
      Given("interactive Story of event that allows to buy the terrains")
      val terrain = gameSession.getPlayerTerrain(PLAYER_1)
      val explorer = gameSession.getFreshBehaviourExplorer(PLAYER_1)
      val story = explorer.currentStories.head.head
      assert(story.isInstanceOf[InteractiveEventStory])
      val interactiveStory = story.asInstanceOf[InteractiveEventStory]

      When("when player have money to buy the terrain")
      val terrainPrice = terrain.asInstanceOf[Purchasable].price
      assert(gameSession.gameBank.getMoneyOfPlayer(explorer.playerId) >= terrainPrice)

      Then("event interaction return OK")
      assert(interactiveStory.interactions.head(explorer.playerId) == Result.OK)

      When("player have no money to buy the terrain")
      gameSession.gameBank.makeTransaction(explorer.playerId, amount = GameSessionHelper.playerInitialMoney)

      Then("event interaction return an ERR")
      assert(interactiveStory.interactions.head(explorer.playerId) match
        case Result.ERR(_) => true
        case _ => false
      )
    }

    Scenario("Player 1 buy a terrain") {
      Given("Terrain has not an owner and player 1 has money to buy explorer ")
      val terrainPrice = gameSession.getPlayerTerrain(PLAYER_1).asInstanceOf[Purchasable].price
      assert(gameSession.gameBank.getMoneyOfPlayer(PLAYER_1) >= terrainPrice)
      assert(purchasableTerrain.owner.isEmpty)

      When("player buy the terrain")
      gameSession.getFreshBehaviourExplorer(PLAYER_1).next()

      Then("player 1 become owner of the terrain")
      assert(purchasableTerrain.owner.nonEmpty)
      assert(purchasableTerrain.owner.get == PLAYER_1)

      Then("player 1 spend his money")
      assert(gameSession.gameBank.getMoneyOfPlayer(PLAYER_1) == GameSessionHelper.playerInitialMoney - TERRAIN_PRICE)
    }

    Scenario("Player  has not money to buy the terrain") {
      Given("player lose its money")
      gameSession.gameBank.makeTransaction(PLAYER_1, amount = GameSessionHelper.playerInitialMoney)
      When("player try to buy terrain")
      Then("exception is lunched")
      assertThrows[IllegalStateException](gameSession.getFreshBehaviourExplorer(PLAYER_1).next())

    }

    Scenario("When terrain is bought another player on this terrain must see the rent event") {
      Given("player 1 buys the terrain")
      gameSession.getFreshBehaviourExplorer(PLAYER_1).next()

      When("player 2 arrived on the terrain")
      val explorer = gameSession.getFreshBehaviourExplorer(PLAYER_2)

      Then("player 2 see pay rent event that cannot be skipped")
      assert(explorer.currentEvents.nonEmpty)
      assert(explorer.currentStories.head.head == rentStory)
      assert(!explorer.canEndExploring)
    }
    
    Scenario("Player 2 pay rent to player 1") {
      Given("player 1 buys the terrain")
      gameSession.getFreshBehaviourExplorer(PLAYER_1).next()

      When("player 2 pay the rent")
      val explorer = gameSession.getFreshBehaviourExplorer(PLAYER_2)
      explorer.next()

      Then("player 2 loses his money and player 1 receives the payment")
      assert(
        gameSession.gameBank.getMoneyOfPlayer(PLAYER_1) == GameSessionHelper.playerInitialMoney - TERRAIN_PRICE + RENT
      )
      assert(gameSession.gameBank.getMoneyOfPlayer(PLAYER_2) == GameSessionHelper.playerInitialMoney - RENT)
    }
  }
