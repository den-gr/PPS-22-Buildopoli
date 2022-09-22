package example.controller

import lib.behaviour.BehaviourModule.Behaviour
import lib.behaviour.event.{BasicEventFactory, EventFactory}
import lib.behaviour.event.story.EventStoryModule.EventStory
import lib.behaviour.factory.{BasicBehaviourFactory, BehaviourFactory}
import lib.gameManagement.gameSession.GameSession
import lib.terrain.Mortgage.DividePriceMortgage
import lib.terrain.RentStrategy.RentStrategyPreviousPriceMultiplier
import lib.terrain.{Buildable, Purchasable, Terrain, TerrainInfo, Token}

/** Create terrain for one specific game setup
  */
trait TerrainInitializer:
  /** @return
    *   an ordered sequence of game terrains
    */
  def buildGameTerrains(): Seq[Terrain]

object TerrainInitializer:
  def apply(gameSession: GameSession): TerrainInitializer = TerrainInitializerImpl(gameSession)

  private class TerrainInitializerImpl(gameSession: GameSession) extends TerrainInitializer:
    private val eventFactory = EventFactory(gameSession)
    private val behaviourFactory = BehaviourFactory(gameSession)

    override def buildGameTerrains(): Seq[Terrain] =
      var terrains: Seq[Terrain] = Seq()
      val STATION_GROUP = "station"
      val BUILDABLE_GROUP = "buildable"
      terrains = terrains :+ createEmptyTerrain()
      terrains = terrains :+ createSimpleStreet("University street", 100, BUILDABLE_GROUP)
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

    private def createSimpleStreet(streetName: String, price: Int, group: String): Terrain =
      val buyStory = EventStory(s"You can buy terrain on $streetName", "Buy terrain")
      val rentStory = EventStory(s"You ara at $streetName, you must puy rent to the owner", "Pay rent")
      val errMsg = "You have not enough money to pay for the rent"
      val behaviour = behaviourFactory.PurchasableTerrainBehaviour(rentStory, errMsg, buyStory)
      val purchasableTerrain = Purchasable(
        Terrain(TerrainInfo(streetName), behaviour),
        price,
        group,
        DividePriceMortgage(price, 2),
        lib.terrain.RentStrategy.BasicRentStrategyFactor(100, 2)
      )
      val token = Token(Seq("house", "hotel"), Seq(2, 1), Seq(Seq(50, 50), Seq(100)), Seq(25, 50))
      Buildable(purchasableTerrain, token)
