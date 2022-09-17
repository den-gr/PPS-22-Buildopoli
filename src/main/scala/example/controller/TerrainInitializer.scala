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
  def buildGameTerrains(): Seq[Terrain]

object TerrainInitializer:
  def apply(gameSession: GameSession): TerrainInitializer = TerrainInitializerImpl(gameSession)

  private case class TerrainInitializerImpl(gameSession: GameSession) extends TerrainInitializer:
    private val eventFactory = EventFactory(gameSession)
    private val behaviourFactory = BehaviourFactory(gameSession)

    override def buildGameTerrains(): Seq[Terrain] =
      var terrains: Seq[Terrain] = Seq()
      val STATION_GROUP = "station"
      terrains = terrains :+ createEmptyTerrain()
      terrains = terrains :+ createWithdrawMoneyTerrain(50)
      terrains = terrains :+ createTransportStationTerrain("Train station", 300, STATION_GROUP)
      terrains = terrains :+ createWithdrawMoneyTerrain(100)
      terrains = terrains :+ createTransportStationTerrain("Bus station", 300, STATION_GROUP)
      terrains

    private def createWithdrawMoneyTerrain(amount: Int): Terrain =
      val story = EventStory(s"You spend $amount money on a party", "Oh, noo")
      val behaviour = Behaviour(eventFactory.WithdrawMoneyEvent(story, amount))
      Terrain(TerrainInfo("Party"), behaviour)

    private def createEmptyTerrain(): Terrain = Terrain(TerrainInfo("Go"), Behaviour())

    private def createTransportStationTerrain(stationName: String, price: Int, group: String): Terrain =
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
