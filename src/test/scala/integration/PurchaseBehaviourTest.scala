package integration

import org.scalatest.BeforeAndAfterEach
import org.scalatest.funsuite.AnyFunSuite
import terrain.Mortgage.DividePriceMortgage
import terrain.RentStrategy.RentStrategyWithBonus
import terrain.{Purchasable, Terrain, TerrainInfo}
import behaviour.BehaviourModule.*
import behaviour.event.EventFactory
import behaviour.event.EventStoryModule.{EventStory, Interaction, InteractiveEventStory, Result}
import gameManagement.gameSession.GameSession
import util.GameSessionHelper.DefaultGameSession

class PurchaseBehaviourTest extends AnyFunSuite with BeforeAndAfterEach:
  private val PLAYER_1 = 1
  private val PLAYER_2 = 2
  val POSITION_0 = 0
  val POSITION_1 = 1
  private var dumbTerrain: Terrain = _
  private var purchasableTerrain: Purchasable = _
  private var gameSession: GameSession = _

  override def beforeEach(): Unit =
    gameSession = DefaultGameSession(2)

    val TERRAIN_PRICE = 100

    val story: EventStory = EventStory("Do you want but this terrain?", "Buy")
    val behaviour: Behaviour = Behaviour(EventFactory(gameSession).BuyTerrainEvent(story))

    dumbTerrain = Terrain(TerrainInfo("Dumb terrain", POSITION_1), behaviour)
    val t: Terrain = Terrain(TerrainInfo("vicolo corto", POSITION_0), behaviour)
    purchasableTerrain =
      Purchasable(t, TERRAIN_PRICE, "fucsia", DividePriceMortgage(1000, 3), RentStrategyWithBonus(50, 20))
    gameSession.gameStore.putTerrain(purchasableTerrain)
    gameSession.gameStore.putTerrain(dumbTerrain)

  test("Buy terrain") {
    assert(purchasableTerrain.owner.isEmpty)
    gameSession.getPlayerTerrain(PLAYER_1).getBehaviourIterator(PLAYER_1).next()
    assert(purchasableTerrain.owner.nonEmpty)
    assert(purchasableTerrain.owner.get == PLAYER_1)
  }

  test("first testwe") {
    gameSession.setPlayerPosition(PLAYER_1, 1, true)
    assertThrows[IllegalStateException](gameSession.getPlayerTerrain(PLAYER_1).getBehaviourIterator(PLAYER_1))
  }
  // TODO GROUP TEST
