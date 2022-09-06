package integration

import behaviour.BehaviourModule.Behaviour
import behaviour.event.{EventFactory, EventGroup}
import behaviour.event.EventStoryModule.EventStory
import gameManagement.gameSession.GameSession
import org.scalatest.BeforeAndAfterEach
import org.scalatest.funsuite.AnyFunSuite
import terrain.Mortgage.DividePriceMortgage
import terrain.RentStrategy.BasicRentStrategyFactor
import terrain.{Purchasable, Terrain, TerrainInfo}
import util.GameSessionHelper.DefaultGameSession

class BuildTokenBehaviourTest extends AnyFunSuite with BeforeAndAfterEach:
  private val PLAYER_1 = 1
  private val POSITION_0 = 0
  private var purchasableTerrain: Purchasable = _
  private val TERRAIN_PRICE = 100
  private val RENT = 50

  private var gameSession: GameSession = _

  override def beforeEach(): Unit =
    gameSession = DefaultGameSession(2)

    val story: EventStory = EventStory("Do you want but this terrain?", "Buy")
    val rentStory: EventStory = EventStory("You need to pay rent", "Pay")
    val factory = EventFactory(gameSession)
    val behaviour: Behaviour = Behaviour(
      EventGroup(factory.BuyTerrainEvent(story), factory.GetRentEvent(rentStory, "Not enough money"))
    )

    val t: Terrain = Terrain(TerrainInfo("vicolo corto", POSITION_0), behaviour)
    purchasableTerrain =
      Purchasable(t, TERRAIN_PRICE, "fucsia", DividePriceMortgage(1000, 3), BasicRentStrategyFactor(RENT, 1))
    gameSession.gameStore.putTerrain(purchasableTerrain)
