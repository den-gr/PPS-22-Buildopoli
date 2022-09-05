package integration

import org.scalatest.BeforeAndAfterEach
import org.scalatest.funsuite.AnyFunSuite
import terrain.Mortgage.DividePriceMortgage
import terrain.RentStrategy.{BasicRentStrategyFactor, RentStrategyWithBonus}
import terrain.{GroupManager, Purchasable, Terrain, TerrainInfo}
import behaviour.BehaviourModule.*
import behaviour.event.{EventFactory, EventGroup}
import behaviour.event.EventStoryModule.{EventStory, Interaction, InteractiveEventStory, Result}
import gameManagement.gameSession.GameSession
import util.GameSessionHelper
import util.GameSessionHelper.DefaultGameSession

//todo The FeatureSpec style
// extends AnyFeatureSpec with GivenWhenThen
class PurchaseBehaviourTest extends AnyFunSuite with BeforeAndAfterEach:
  private val PLAYER_1 = 1
  private val PLAYER_2 = 2
  private val POSITION_0 = 0
  private val POSITION_1 = 1
  private var dumbTerrain: Terrain = _
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

    dumbTerrain = Terrain(TerrainInfo("Dumb terrain", POSITION_1), behaviour)
    val t: Terrain = Terrain(TerrainInfo("vicolo corto", POSITION_0), behaviour)
    purchasableTerrain =
      Purchasable(t, TERRAIN_PRICE, "fucsia", DividePriceMortgage(1000, 3), BasicRentStrategyFactor(RENT, 1))
    gameSession.gameStore.putTerrain(purchasableTerrain)
    gameSession.gameStore.putTerrain(dumbTerrain)

  test("Verify the buy terran interaction") {
    val it = gameSession.getPlayerTerrain(PLAYER_1).getBehaviourIterator(PLAYER_1)
    val story = getStories(it.currentEvents, it.playerId).head.head
    assert(story.isInstanceOf[InteractiveEventStory])
    val interactiveStory = story.asInstanceOf[InteractiveEventStory]
    assert(interactiveStory.interactions.head(it.playerId) == Result.OK)

    gameSession.gameBank.decreasePlayerMoney(it.playerId, GameSessionHelper.playerInitialMoney)
    assert(interactiveStory.interactions.head(it.playerId) match
      case Result.ERR(_) => true
      case _ => false
    )
  }

  test("Test buying of terrain by player 1") {
    assert(gameSession.gameBank.getMoneyForPlayer(PLAYER_1) == GameSessionHelper.playerInitialMoney)
    assert(purchasableTerrain.owner.isEmpty)
    gameSession.getPlayerTerrain(PLAYER_1).getBehaviourIterator(PLAYER_1).next() // buy
    assert(purchasableTerrain.owner.nonEmpty)
    assert(purchasableTerrain.owner.get == PLAYER_1)
    assert(gameSession.gameBank.getMoneyForPlayer(PLAYER_1) == GameSessionHelper.playerInitialMoney - TERRAIN_PRICE)
    println(purchasableTerrain.computeTotalRent(GroupManager(Array(purchasableTerrain))))
  }

  test(
    "When terrain is bought another player see pay rent event, interaction return OK when player have money to pay rent "
  ) {
    gameSession.getPlayerTerrain(PLAYER_1).getBehaviourIterator(PLAYER_1).next()
    val it = gameSession.getPlayerTerrain(PLAYER_2).getBehaviourIterator(PLAYER_2)
    assert(it.currentEvents.nonEmpty)
    val interactiveStory = getStories(it.currentEvents, it.playerId).head.head.asInstanceOf[InteractiveEventStory]
    assert(interactiveStory.interactions.head(it.playerId) == Result.OK)

    gameSession.gameBank.decreasePlayerMoney(it.playerId, GameSessionHelper.playerInitialMoney)
    assert(interactiveStory.interactions.head(it.playerId) match
      case Result.ERR(_) => true
      case _ => false
    )
  }

  test("Player 2 pay rent to player 1") {
    gameSession.getPlayerTerrain(PLAYER_1).getBehaviourIterator(PLAYER_1).next()
    val it = gameSession.getPlayerTerrain(PLAYER_2).getBehaviourIterator(PLAYER_2)
    it.next()
    assert(
      gameSession.gameBank.getMoneyForPlayer(PLAYER_1) == GameSessionHelper.playerInitialMoney - TERRAIN_PRICE + RENT
    )
    assert(gameSession.gameBank.getMoneyForPlayer(PLAYER_2) == GameSessionHelper.playerInitialMoney - RENT)
  }

  test("Buy terrain event is not compatible with not purchasable terrains") {
    gameSession.setPlayerPosition(PLAYER_1, 1, true)
    assertThrows[IllegalStateException](gameSession.getPlayerTerrain(PLAYER_1).getBehaviourIterator(PLAYER_1))
  }
