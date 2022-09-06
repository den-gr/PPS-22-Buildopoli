package behaviour.event

import behaviour.event.*
import EventModule.*
import EventStoryModule.*
import Event.*
import gameManagement.gameSession.GameSession
import org.slf4j.{Logger, LoggerFactory}
import terrain.{Buildable, GroupManager, Purchasable}
import terrain.PurchasableState.*

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
    private val logger: Logger = gameSession.logger
    private val gameTurn = gameSession.gameTurn
    private val bank = gameSession.gameBank
    private val dice = gameSession.dice

    override def WithdrawMoneyEvent(story: EventStory, amount: Int): Event =
      val withdrawalStrategy: EventStrategy = playerId => bank.makeTransaction(playerId, amount = amount)
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
          logger.info(escapeSuccessMsg(playerId.toString))
          gameSession.setPlayerPosition(playerId, dice.rollMoreDice(2), true)
        else logger.info(escapeFailMsg(playerId.toString))
      val escapePrecondition: EventPrecondition = gameTurn.getRemainingBlockedMovements(_).nonEmpty
      Event(story, escapeStrategy, escapePrecondition)

    override def BuyTerrainEvent(story: EventStory): Event =
      val precondition: EventPrecondition = playerId =>
        gameSession.getPlayerTerrain(playerId) match
          case t: Purchasable if t.state == IN_BANK => true
          case _: Purchasable => false
          case t => throw IllegalStateException(s"BuyTerrainEvent is not compatible with ${t.getClass}")

      val interaction: Interaction = playerId =>
        val playerMoney = gameSession.gameBank.getMoneyForPlayer(playerId)
        gameSession.getPlayerTerrain(playerId) match
          case t: Purchasable if playerMoney >= t.price => Result.OK
          case _: Purchasable => Result.ERR("Not enough money")

      val interactiveStory = EventStory(story, Seq(interaction))

      val strategy: EventStrategy = playerId =>
        gameSession.getPlayerTerrain(playerId) match
          case t: Purchasable if t.state != IN_BANK =>
            throw IllegalStateException("Player can not buy already purchased terrain")
          case t: Purchasable =>
            if bank.getMoneyForPlayer(playerId) >= t.price then
              bank.makeTransaction(playerId, amount = t.price)
              t.changeOwner(Some(playerId))
            else
              throw IllegalStateException(
                s"Player $playerId has not enough money =>  ${bank.getMoneyForPlayer(playerId)} but need ${t.price}"
              )
          case t => throw IllegalStateException(s"BuyTerrainEvent is not compatible with ${t.getClass}")

      Event(interactiveStory, strategy, precondition)

    override def GetRentEvent(story: EventStory, notMoneyErrMsg: String): Event =
      val precondition: EventPrecondition = playerId =>
        gameSession.getPlayerTerrain(playerId) match
          case t: Purchasable if t.state == OWNED && t.owner.get != playerId => true
          case _: Purchasable => false
          case t => throw IllegalStateException(s"GetRentEvent is not compatible with ${t.getClass}")

      val interaction: Interaction = playerId =>
        val playerMoney = gameSession.gameBank.getMoneyForPlayer(playerId)
        gameSession.getPlayerTerrain(playerId) match
          case t: Purchasable if playerMoney >= t.computeTotalRent(gameSession.getGroupManager) =>
            Result.OK
          case _: Purchasable => Result.ERR(notMoneyErrMsg)

      val interactiveStory = EventStory(story, Seq(interaction))

      val strategy: EventStrategy = playerId =>
        val playerMoney = gameSession.gameBank.getMoneyForPlayer(playerId)
        gameSession.getPlayerTerrain(playerId) match
          case t: Purchasable if playerMoney >= t.computeTotalRent(gameSession.getGroupManager) =>
            bank.makeTransaction(
              playerId,
              t.owner.get,
              t.computeTotalRent(GroupManager(Array(t)))
            )
          case _ => throw IllegalStateException("Not enough money for pay the rent")

      Event(interactiveStory, strategy, precondition)

    override def BuildTokenEvent(storyDescription: String): Event =
//      val storyGenerator: StoryGenerator = playerId =>
//        val interactions: Interaction = Seq()
//        for
//           val terrain <- groupManager.terrainsOwnerCanBuildOn(playerId)
//        yield
//
//
//        EventStory(storyDescription, "")
//      val precondition: EventPrecondition = playerId =>
//        gameSession.getPlayerTerrain(playerId) match
//          case t: Buildable if => true //todo temporal group manger
      ???
