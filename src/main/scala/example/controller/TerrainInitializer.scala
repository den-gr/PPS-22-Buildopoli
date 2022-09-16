package example.controller

import lib.behaviour.BehaviourModule.Behaviour
import lib.behaviour.event.{BasicEventFactory, EventFactory}
import lib.behaviour.event.EventStoryModule.EventStory
import lib.behaviour.factory.{BasicBehaviourFactory, BehaviourFactory}
import lib.gameManagement.gameSession.GameSession
import lib.terrain.Mortgage.DividePriceMortgage
import lib.terrain.RentStrategy.RentStrategyPreviousPriceMultiplier
import lib.terrain.{Purchasable, Terrain, TerrainInfo}

trait TerrainInitializer:
  def insertGameTerrains(gameSession: GameSession): Seq[Terrain]

object TerrainInitializer extends TerrainInitializer:

  def insertGameTerrains(gameSession: GameSession): Seq[Terrain] =
    val eventFactory = EventFactory(gameSession)
    val behaviourFactory = BehaviourFactory(gameSession)

    var terrains: Seq[Terrain] = Seq()
    val STATION_GROUP = "station"
    terrains = terrains :+ createEmptyTerrain()
    terrains = terrains :+ createWithdrawMoneyTerrain(eventFactory)(50)
    terrains = terrains :+ createTransportStationTerrain(behaviourFactory)("Train station", 300, STATION_GROUP)
    terrains = terrains :+ createWithdrawMoneyTerrain(eventFactory)(100)
    terrains = terrains :+ createTransportStationTerrain(behaviourFactory)("Bus station", 300, STATION_GROUP)
    terrains

  private def createWithdrawMoneyTerrain(eventFactory: BasicEventFactory)(amount: Int): Terrain =
    val story = EventStory(s"You spend $amount money on a party", "Oh, noo")
    val behaviour = Behaviour(eventFactory.WithdrawMoneyEvent(story, amount))
    Terrain(TerrainInfo("Party"), behaviour)

  private def createEmptyTerrain(): Terrain = Terrain(TerrainInfo("Go"), Behaviour())

  private def createTransportStationTerrain(
      behaviourFactory: BasicBehaviourFactory
  )(stationName: String, price: Int, group: String): Terrain =
    val buyStory = EventStory(s"You have an incredible opportunity to buy $stationName", "Buy station")
    val rentStory = EventStory(s"You are at $stationName and must pay for the ticket", "Pay for ticket")
    val errMsg = "You have not enough money to pay for the ticket"
    val behaviour = behaviourFactory.PurchasableTerrainBehaviour(rentStory, errMsg, buyStory)
    Purchasable(
      Terrain(TerrainInfo(stationName), behaviour),
      price,
      group,
      DividePriceMortgage(price, 2),
      RentStrategyPreviousPriceMultiplier(50, 2)
    )
