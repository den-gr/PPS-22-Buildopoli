package lib.behaviour.event

import lib.behaviour.event.*
import lib.behaviour.event.EventModule.Event
import lib.behaviour.event.EventModule.*
import lib.behaviour.event.story.EventStoryModule.*
import lib.behaviour.event.story.InteractiveEventStoryModule.{Result, *}
import lib.gameManagement.gameSession.GameSession
import lib.gameManagement.gameTurn.GameJail
import lib.gameManagement.log.GameLogger
import lib.terrain.*
import lib.terrain.PurchasableState.*

/** Give access to static factory constructor of events and allows create a [[BasicEventFactory]] instance
  */
object EventFactory:
  /** A simple type for generating personalized messages. Typically take in input player id and return a personalized
   * event message
   */
  type EventLogMsg = String => String

  /** @param gameSession
    *   Current game session
    * @return
    *   factory for event creation
    */
  def apply(gameSession: GameSession): BasicEventFactory = EventFactoryImpl(gameSession)

  /** Creation of an informative event, that can be useful as an introduction to a chain of events
    * @param story
    *   event description
    * @param precondition
    *   define when this event will be visible to a player
    * @return
    *   event that not have any logic
    */
  def InfoEvent(story: EventStory, precondition: EventPrecondition): Event =
    Event(story, precondition = precondition)

  private class EventFactoryImpl(gameSession: GameSession) extends BasicEventFactory:
    private val logger: GameLogger = gameSession.logger
    private val gameTurn = gameSession.gameTurn.asInstanceOf[GameJail]
    private val bank = gameSession.gameBank
    private val dice = gameSession.dice
    private val gameStore = gameSession.gameStore

    private def groupMng: GroupManager = gameSession.getGroupManager

    override def WithdrawMoneyEvent(story: EventStory, amount: Int, resultOfWithdrawingMsg: EventLogMsg): Event =
      val withdrawalStrategy: EventStrategy = playerId =>
        bank.makeTransaction(playerId, amount = amount)
        logger.log(resultOfWithdrawingMsg(playerId.toString) + bank.getMoneyOfPlayer(playerId))
      Event(story, withdrawalStrategy)

    override def ImprisonEvent(story: EventStory, blockingTurns: Int): Event =
      val imprisonStrategy: EventStrategy = playerId =>
        gameTurn.getRemainingBlockedMovements(playerId) match
          case None =>
            gameTurn.lockPlayer(playerId, blockingTurns)
          case _ =>
      Event(story, imprisonStrategy)

    override def EscapeEvent(story: EventStory, escapeSuccessMsg: EventLogMsg, escapeFailMsg: EventLogMsg): Event =
      val escapeStrategy: EventStrategy = playerId =>
        if dice.rollOneDice() == dice.rollOneDice() then
          gameTurn.liberatePlayer(playerId)
          logger.log(escapeSuccessMsg(playerId.toString))
          gameSession.movePlayer(playerId, steps = dice.rollMoreDice(2))
        else logger.log(escapeFailMsg(playerId.toString))
      val escapePrecondition: EventPrecondition = gameTurn.getRemainingBlockedMovements(_).nonEmpty
      Event(story, escapeStrategy, escapePrecondition)

    override def BuyTerrainEvent(story: EventStory, notMoneyErrMsg: String): Event =
      val precondition: EventPrecondition = playerId =>
        gameSession.getPlayerTerrain(playerId) match
          case t: Purchasable if t.state == IN_BANK => true
          case _: Purchasable => false
          case t => throw IllegalStateException(s"BuyTerrainEvent is not compatible with ${t.getClass}")

      val interaction: Interaction = playerId =>
        val playerMoney = gameSession.gameBank.getMoneyOfPlayer(playerId)
        gameSession.getPlayerTerrain(playerId) match
          case t: Purchasable if playerMoney >= t.price => Result.OK
          case _: Purchasable => Result.ERR(notMoneyErrMsg)

      val interactiveStory = EventStory(story, Seq(interaction))

      val strategy: EventStrategy = playerId =>
        gameSession.getPlayerTerrain(playerId) match
          case t: Purchasable if t.state != IN_BANK =>
            throw IllegalStateException("Player can not buy already purchased terrain")
          case t: Purchasable =>
            if bank.getMoneyOfPlayer(playerId) >= t.price then
              bank.makeTransaction(playerId, amount = t.price)
              t.changeOwner(Some(playerId))
            else
              throw IllegalStateException(
                s"Player $playerId has not enough money =>  ${bank.getMoneyOfPlayer(playerId)} but need ${t.price}"
              )
          case t => throw IllegalStateException(s"BuyTerrainEvent is not compatible with ${t.getClass}")

      Event(interactiveStory, strategy, precondition)

    override def GetRentEvent(story: EventStory): Event =
      val precondition: EventPrecondition = playerId =>
        gameSession.getPlayerTerrain(playerId) match
          case t: Purchasable if t.state == OWNED && t.owner.get != playerId => true
          case _: Purchasable => false
          case t => throw IllegalStateException(s"GetRentEvent is not compatible with ${t.getClass}")

      val strategy: EventStrategy = playerId =>
        val playerMoney = gameSession.gameBank.getMoneyOfPlayer(playerId)
        gameSession.getPlayerTerrain(playerId) match
          case t: Purchasable if playerMoney >= t.computeTotalRent(groupMng) =>
            bank.makeTransaction(
              playerId,
              t.owner.get,
              t.computeTotalRent(GroupManager(Array(t)))
            )
          case _ => throw IllegalStateException("Not enough money for pay the rent")

      Event(story, strategy, precondition)

    override def BuildTokenEvent(
        terrainSelectionStory: String,
        tokenSelectionStory: String,
        numberOfTokenSelectionStory: String,
        notEnoughMoneyMsgErr: String
    ): Event =
      val eventPrecondition: EventPrecondition = playerId =>
        groupMng.terrainsOwnerCanBuildOn(playerId).exists(_.canBuild(groupMng))

      val storyGenerator: StoryGenerator = playerId =>
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
                    .map(e =>
                      notEnoughMoneyMsgErr + s" => $e (${bank.getMoneyOfPlayer(playerId)}/${terrain.tokenBuyingPrice(e)})"
                    )
                    .mkString("\n")
                )
              else
                gameStore.userInputs.addTailInputEvent(terrain)
                Result.OK
            val choice: String = terrain.basicInfo.name
            (choice, interaction)
        EventStory(terrainSelectionStory, choicesWithInteractions)

      val storyGenerator2: StoryGenerator = playerId =>
        gameStore.userInputs.getHeadElement match
          case terrain: Buildable =>
            val choicesWithInteractions =
              for tokenName <- terrain.listAvailableToken()
              yield
                val interaction = (_: Int) =>
                  if terrain.tokenBuyingPrice(tokenName) > bank.getMoneyOfPlayer(playerId) then
                    Result.ERR(
                      notEnoughMoneyMsgErr + s" => $tokenName (${bank.getMoneyOfPlayer(playerId)}/${terrain.tokenBuyingPrice(tokenName)})"
                    )
                  else
                    gameStore.userInputs.addTailInputEvent(tokenName)
                    Result.OK
                val choice: String = tokenName
                (choice, interaction)
            EventStory(tokenSelectionStory, choicesWithInteractions)
          case _ => throw EventInputException()

      val storyGenerator3: StoryGenerator = playerId =>
        val terrainInput = gameStore.userInputs.getHeadElement
        gameStore.userInputs.removeHeadElement()
        val tokenNameInput = gameStore.userInputs.getHeadElement
        gameStore.userInputs.removeHeadElement()
        gameStore.userInputs.addTailInputEvent(terrainInput)
        gameStore.userInputs.addTailInputEvent(tokenNameInput)
        if !tokenNameInput.isInstanceOf[String] || !terrainInput.isInstanceOf[Buildable] then
          throw EventInputException()
        val terrain = terrainInput.asInstanceOf[Buildable]
        val tokenName = tokenNameInput.asInstanceOf[String]
        val choicesWithInteractions =
          for n <- 1 to terrain.remainingTokens(tokenName)
          yield
            val interaction = (_: Int) =>
              if terrain.tokenBuyingPrice(tokenName) * n > bank.getMoneyOfPlayer(playerId) then
                Result.ERR(
                  notEnoughMoneyMsgErr + s"(${bank.getMoneyOfPlayer(playerId) * n}/${terrain.tokenBuyingPrice(tokenName)})"
                )
              else
                gameStore.userInputs.addTailInputEvent(n)
                Result.OK
            val choice: String = n.toString
            (choice, interaction)
        EventStory(numberOfTokenSelectionStory, choicesWithInteractions)

      val eventStrategy: EventStrategy = playerId =>
        val terrainInput = gameStore.userInputs.getHeadElement
        gameStore.userInputs.removeHeadElement()
        val tokenNameInput = gameStore.userInputs.getHeadElement
        gameStore.userInputs.removeHeadElement()
        val inputNumBuilding = gameStore.userInputs.getHeadElement
        gameStore.userInputs.removeHeadElement()

        if !tokenNameInput.isInstanceOf[String] || !terrainInput.isInstanceOf[Buildable] || !inputNumBuilding
            .isInstanceOf[Int]
        then throw EventInputException()
        val numBuilding = inputNumBuilding.asInstanceOf[Int]
        val terrain = terrainInput.asInstanceOf[Buildable]
        val tokenName = tokenNameInput.asInstanceOf[String]

        bank.makeTransaction(playerId, amount = terrain.tokenBuyingPrice(tokenName) * numBuilding)
        terrain.addToken(tokenName, numBuilding)

      val thirdEvent = EventGroup(Event(storyGenerator3, eventStrategy))
      val secondEvent = EventGroup(Event(storyGenerator2, nextEvent = Some(thirdEvent)))
      Event(storyGenerator, precondition = eventPrecondition, nextEvent = Some(secondEvent))

    override def MortgageEvent(eventDescription: String): Event =
      val precondition: EventPrecondition = playerId =>
        gameStore.terrainList
          .filter(_.isInstanceOf[Purchasable])
          .map(_.asInstanceOf[Purchasable])
          .filter(_.state == OWNED)
          .exists(_.owner.get == playerId)

      val storyGenerator: StoryGenerator = playerId =>
        val ownedTerrains = gameStore.terrainList
          .filter(_.isInstanceOf[Purchasable])
          .map(_.asInstanceOf[Purchasable])
          .filter(_.state == OWNED)
          .filter(_.owner.get == playerId)
        val choicesWithInteractions =
          for terrain <- ownedTerrains
          yield
            val interaction = (_: Int) =>
              gameStore.userInputs.addTailInputEvent(terrain)
              Result.OK
            val choice: String = terrain.basicInfo.name + s" | (+${terrain.computeMortgage} €)"
            (choice, interaction)
        EventStory(eventDescription, choicesWithInteractions)

      val eventStrategy: EventStrategy = playerId =>
        val terrainInput = gameStore.userInputs.getHeadElement
        gameStore.userInputs.removeHeadElement()
        if !terrainInput.isInstanceOf[Purchasable] then EventInputException()
        val terrain = terrainInput.asInstanceOf[Purchasable]
        bank.makeTransaction(receiverId = playerId, amount = terrain.computeMortgage)
        terrain.mortgage()

      Event(storyGenerator, eventStrategy, precondition = precondition)

    override def RetrieveFromMortgageEvent(eventDescription: String, notMoneyErrMsg: String): Event =
      val precondition: EventPrecondition = playerId =>
        gameStore.terrainList
          .filter(_.isInstanceOf[Purchasable])
          .map(_.asInstanceOf[Purchasable])
          .filter(_.state == MORTGAGED)
          .exists(_.owner.get == playerId)

      val storyGenerator: StoryGenerator = playerId =>
        val mortgagedTerrains = gameStore.terrainList
          .filter(_.isInstanceOf[Purchasable])
          .map(_.asInstanceOf[Purchasable])
          .filter(_.state == MORTGAGED)
          .filter(_.owner.get == playerId)
        val choicesWithInteractions =
          for terrain <- mortgagedTerrains
          yield
            val interaction = (_: Int) =>
              if bank.getMoneyOfPlayer(playerId) < terrain.computeMortgage then Result.ERR(notMoneyErrMsg)
              else
                gameStore.userInputs.addTailInputEvent(terrain)
                Result.OK
            val choice: String = terrain.basicInfo.name + s" | (-${terrain.computeMortgage} €)"
            (choice, interaction)
        EventStory(eventDescription, choicesWithInteractions)

      val eventStrategy: EventStrategy = playerId =>
        val terrainInput = gameStore.userInputs.getHeadElement
        gameStore.userInputs.removeHeadElement()
        if !terrainInput.isInstanceOf[Purchasable] then EventInputException()
        val terrain = terrainInput.asInstanceOf[Purchasable]
        bank.makeTransaction(playerId, amount = terrain.computeMortgage)
        terrain.removeMortgage()
      Event(storyGenerator, eventStrategy, precondition = precondition)
