package lib.behaviour.factory.constructor

import lib.behaviour.event.EventModule.{EventStrategy, StoryGenerator}
import lib.behaviour.event.story.EventStoryModule.EventStory
import lib.terrain.{Purchasable, Terrain, TerrainUtils}
import lib.terrain.PurchasableState.*
import lib.behaviour.event.story.InteractiveEventStoryModule.Result
import lib.gameManagement.gameBank.Bank
import lib.gameManagement.gameStore.GameStore
import lib.behaviour.event.*
import lib.gameManagement.gameStore.gameInputs.GameInputs

object MortgageEventConstructor:
  def mortgageStoryGenerator(
      eventDescription: String,
      gameStore: GameStore
  ): StoryGenerator = playerId =>
    val ownedTerrains = TerrainUtils.filterPurchasable(gameStore.terrainList, playerId, OWNED)
    val choicesWithInteractions =
      for terrain <- ownedTerrains
      yield
        val interaction = (_: Int) =>
          gameStore.userInputs.addTailInputEvent(terrain)
          Result.OK
        val choice: String = terrain.basicInfo.name + s" | (+${terrain.computeMortgage} €)"
        (choice, interaction)
    EventStory(eventDescription, choicesWithInteractions)

  def mortgageEventStrategy(gameInputs: GameInputs, bank: Bank): EventStrategy = playerId =>
    val terrainInput = getInput((gameInputs))
    if !terrainInput.isInstanceOf[Purchasable] then EventInputException()
    val terrain = terrainInput.asInstanceOf[Purchasable]
    bank.makeTransaction(receiverId = playerId, amount = terrain.computeMortgage)
    terrain.mortgage()

  def retrieveFromMortgageEventStory(
      eventDescription: String,
      errMsg: String,
      gameStore: GameStore,
      bank: Bank,
      gameInputs: GameInputs
  ): StoryGenerator =
    playerId =>
      val mortgagedTerrains = TerrainUtils.filterPurchasable(gameStore.terrainList, playerId, MORTGAGED)
      val choicesWithInteractions =
        for terrain <- mortgagedTerrains
        yield
          val interaction = (_: Int) =>
            if bank.getMoneyOfPlayer(playerId) < terrain.computeMortgage then Result.ERR(errMsg)
            else
              gameInputs.addTailInputEvent(terrain)
              Result.OK
          val choice: String = terrain.basicInfo.name + s" | (-${terrain.computeMortgage} €)"
          (choice, interaction)
      EventStory(eventDescription, choicesWithInteractions)

  def retrieveMortgageEventStrategy(gameInputs: GameInputs, bank: Bank): EventStrategy = playerId =>
    val terrainInput = getInput(gameInputs)
    if !terrainInput.isInstanceOf[Purchasable] then EventInputException()
    val terrain = terrainInput.asInstanceOf[Purchasable]
    bank.makeTransaction(playerId, amount = terrain.computeMortgage)
    terrain.removeMortgage()

  private def getInput(gameInputs: GameInputs): Any =
    val input = gameInputs.getHeadElement
    gameInputs.removeHeadElement()
    input
