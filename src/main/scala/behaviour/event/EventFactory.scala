package behaviour.event

import behaviour.event.*
import EventModule.*
import EventStoryModule.*
import Event.*
import gameManagement.gameSession.GameSession
import org.slf4j.{Logger, LoggerFactory}

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
    * @param condition
    *   define when this event will be visible to a player
    * @return
    *   event that not have any logic
    */
  def InfoEvent(story: EventStory, condition: EventPrecondition): Event =
    Event(Scenario(story), condition)

  class EventFactoryImpl(gameSession: GameSession) extends BasicEventFactory:
    val logger: Logger = LoggerFactory.getLogger(this.getClass)
    private val gameTurn = gameSession.gameTurn
    private val dice = gameSession.dice

    override def ImprisonEvent(story: EventStory, blockingTurns: Int): Event =
      val imprisonStrategy: Int => Unit = playerId =>
        gameTurn.getRemainingBlockedMovements(playerId) match
          case None =>
            gameTurn.lockPlayer(playerId, blockingTurns)
          case _ =>
      Event(Scenario(imprisonStrategy, story))

    override def EscapeEvent(story: EventStory, escapeSuccessMsg: EventLogMsg, escapeFailMsg: EventLogMsg): Event =
      val escapeStrategy: Int => Unit = playerId =>
        if dice.rollOneDice() == dice.rollOneDice() then
          gameTurn.liberatePlayer(playerId)
          logger.info(escapeSuccessMsg(playerId.toString))
          gameSession.setPlayerPosition(playerId, dice.rollMoreDice(2), true)
        else logger.info(escapeFailMsg(playerId.toString))
      val escapePrecondition: EventPrecondition = gameTurn.getRemainingBlockedMovements(_).nonEmpty
      Event(Scenario(escapeStrategy, story), escapePrecondition)
