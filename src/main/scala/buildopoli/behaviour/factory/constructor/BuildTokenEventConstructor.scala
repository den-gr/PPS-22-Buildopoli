package buildopoli.behaviour.factory.constructor

import buildopoli.behaviour.event.EventModule.{EventStrategy, StoryGenerator}
import buildopoli.behaviour.event.story.EventStoryModule.EventStory
import buildopoli.behaviour.event.story.InteractiveEventStoryModule.Result
import buildopoli.gameManagement.gameBank.Bank
import buildopoli.gameManagement.gameSession.GameSession
import buildopoli.gameManagement.gameStore.GameStore
import buildopoli.terrain.{Buildable, TerrainUtils}
import buildopoli.behaviour.event.*
import buildopoli.gameManagement.gameStore.gameInputs.{GameInputs, UserInputs}

object BuildTokenEventConstructor:

  def chooseTerrainStoryGenerator(
      baseStoryDescription: String,
      errMsg: String,
      gameSession: GameSession
  ): StoryGenerator =
    playerId =>
      val groupMng = gameSession.getGroupManager
      val bank = gameSession.gameBank
      val choicesWithInteractions =
        gameSession.gameStore.terrainList
        for terrain <- groupMng.terrainsOwnerCanBuildOn(playerId).filter(_.canBuild(groupMng))
        yield
          val interaction = (_: Int) =>
            if terrain.listAvailableToken().forall(terrain.tokenBuyingPrice(_) > bank.getMoneyOfPlayer(playerId))
            then
              Result.ERR(
                terrain
                  .listAvailableToken()
                  .map(e => errMsg + s" => $e (${bank.getMoneyOfPlayer(playerId)}/${terrain.tokenBuyingPrice(e)})")
                  .mkString("\n")
              )
            else
              gameSession.gameStore.userInputs.addTailInputEvent(terrain)
              Result.OK
          val choice: String = terrain.basicInfo.name
          (choice, interaction)
      EventStory(baseStoryDescription, choicesWithInteractions)

  def chooseTypeOfTokenStoryGenerator(
      baseStoryDescription: String,
      errMsg: String,
      gameStore: GameStore,
      bank: Bank
  ): StoryGenerator =
    playerId =>
      gameStore.userInputs.getHeadElement match
        case terrain: Buildable =>
          val choicesWithInteractions =
            for tokenName <- terrain.listAvailableToken()
            yield
              val interaction = (_: Int) =>
                if terrain.tokenBuyingPrice(tokenName) > bank.getMoneyOfPlayer(playerId) then
                  Result.ERR(
                    errMsg + s" => $tokenName (${bank.getMoneyOfPlayer(playerId)}/${terrain.tokenBuyingPrice(tokenName)})"
                  )
                else
                  gameStore.userInputs.addTailInputEvent(tokenName)
                  Result.OK
              val choice: String = tokenName
              (choice, interaction)
          EventStory(baseStoryDescription, choicesWithInteractions)
        case _ => throw EventInputException()

  def chooseNumberOfTokenStoryGenerator(
      baseStoryDescription: String,
      errMsg: String,
      userInputs: GameInputs,
      bank: Bank
  ): StoryGenerator =
    playerId =>
      val terrainInput =  getInput(userInputs)
      val tokenNameInput =  getInput(userInputs)

      userInputs.addTailInputEvent(terrainInput)
      userInputs.addTailInputEvent(tokenNameInput)
      if !tokenNameInput.isInstanceOf[String] || !terrainInput.isInstanceOf[Buildable] then throw EventInputException()
      val terrain = terrainInput.asInstanceOf[Buildable]
      val tokenName = tokenNameInput.asInstanceOf[String]
      val choicesWithInteractions =
        for n <- 1 to terrain.remainingTokens(tokenName)
        yield
          val interaction = (_: Int) =>
            if terrain.tokenBuyingPrice(tokenName) * n > bank.getMoneyOfPlayer(playerId) then
              Result.ERR(
                errMsg + s"(${bank.getMoneyOfPlayer(playerId) * n}/${terrain.tokenBuyingPrice(tokenName)})"
              )
            else
              userInputs.addTailInputEvent(n)
              Result.OK
          val choice: String = n.toString
          (choice, interaction)
      EventStory(baseStoryDescription, choicesWithInteractions)

  def eventStrategy(userInputs: GameInputs, bank: Bank): EventStrategy =
    playerId =>
      val terrainInput = getInput(userInputs)
      val tokenNameInput = getInput(userInputs)
      val inputNumBuilding = getInput(userInputs)

      if !tokenNameInput.isInstanceOf[String] || !terrainInput.isInstanceOf[Buildable] || !inputNumBuilding
        .isInstanceOf[Int]
      then throw EventInputException()
      val numBuilding = inputNumBuilding.asInstanceOf[Int]
      val terrain = terrainInput.asInstanceOf[Buildable]
      val tokenName = tokenNameInput.asInstanceOf[String]

      bank.makeTransaction(playerId, amount = terrain.tokenBuyingPrice(tokenName) * numBuilding)
      terrain.addToken(tokenName, numBuilding)  
      
  private def getInput(gameInputs: GameInputs): Any =
    val input = gameInputs.getHeadElement
    gameInputs.removeHeadElement()
    input