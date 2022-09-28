package buildopoli.behaviour.factory.constructor

import buildopoli.behaviour.event.EventModule.{EventStrategy, StoryGenerator}
import buildopoli.behaviour.event.story.EventStoryModule.EventStory
import buildopoli.terrain.{Purchasable, Terrain, TerrainUtils}
import buildopoli.terrain.PurchasableState.*
import buildopoli.behaviour.event.story.InteractiveEventStoryModule.Result
import buildopoli.gameManagement.gameBank.Bank
import buildopoli.gameManagement.gameStore.GameStore
import buildopoli.behaviour.event.*
import buildopoli.gameManagement.gameStore.gameInputs.GameInputs

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
