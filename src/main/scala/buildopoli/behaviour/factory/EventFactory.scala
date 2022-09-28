package buildopoli.behaviour.factory

import buildopoli.behaviour.event.EventModule.{Event, EventPrecondition, EventStrategy, StoryGenerator}
import buildopoli.behaviour.event.story.EventStoryModule.EventStory
import buildopoli.behaviour.event.story.InteractiveEventStoryModule.{Interaction, Result}
import buildopoli.behaviour.event.*
import buildopoli.behaviour.factory.constructor.{BuildTokenEventConstructor, MortgageEventConstructor}
import buildopoli.gameManagement.gameSession.GameSession
import buildopoli.gameManagement.gameTurn.GameJail
import buildopoli.gameManagement.log.GameLogger
import buildopoli.terrain.PurchasableState.*
import buildopoli.terrain.{Buildable, GroupManager, Purchasable, TerrainUtils}

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
        gameSession.getPlayerTerrain(playerId) match
          case t: Purchasable =>
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

      val storyGenerator: StoryGenerator =
        BuildTokenEventConstructor.chooseTerrainStoryGenerator(terrainSelectionStory, notEnoughMoneyMsgErr, gameSession)

      val storyGenerator2: StoryGenerator = BuildTokenEventConstructor.chooseTypeOfTokenStoryGenerator(
        tokenSelectionStory,
        notEnoughMoneyMsgErr,
        gameStore,
        bank
      )
      val storyGenerator3: StoryGenerator = BuildTokenEventConstructor.chooseNumberOfTokenStoryGenerator(
        numberOfTokenSelectionStory,
        notEnoughMoneyMsgErr,
        gameStore.userInputs,
        bank
      )

      val eventStrategy: EventStrategy = BuildTokenEventConstructor.eventStrategy(gameStore.userInputs, bank)

      val thirdEvent = EventGroup(Event(storyGenerator3, eventStrategy))
      val secondEvent = EventGroup(Event(storyGenerator2, nextEvent = Some(thirdEvent)))
      Event(storyGenerator, precondition = eventPrecondition, nextEvent = Some(secondEvent))

    override def MortgageEvent(eventDescription: String): Event =
      val precondition: EventPrecondition = playerId =>
        TerrainUtils.filterPurchasable(gameStore.terrainList, playerId, OWNED).nonEmpty
      val storyGenerator: StoryGenerator = MortgageEventConstructor.mortgageStoryGenerator(eventDescription, gameStore)
      val eventStrategy: EventStrategy = MortgageEventConstructor.mortgageEventStrategy(gameStore.userInputs, bank)
      Event(storyGenerator, eventStrategy, precondition = precondition)

    override def RetrieveFromMortgageEvent(eventDescription: String, notMoneyErrMsg: String): Event =
      val precondition: EventPrecondition = playerId =>
        TerrainUtils.filterPurchasable(gameStore.terrainList, playerId, MORTGAGED).nonEmpty

      val storyGenerator: StoryGenerator = MortgageEventConstructor.retrieveFromMortgageEventStory(
        eventDescription,
        notMoneyErrMsg,
        gameStore,
        bank,
        gameStore.userInputs
      )

      val eventStrategy: EventStrategy =
        MortgageEventConstructor.retrieveMortgageEventStrategy(gameStore.userInputs, bank)
      Event(storyGenerator, eventStrategy, precondition = precondition)
