package behaviour.event

import behaviour.event.*
import EventModule.*
import EventStoryModule.*
import Event.*
import gameManagement.gameSession.GameSession
import org.slf4j.{Logger, LoggerFactory}

object EventFactory:
  def apply(gameSession: GameSession): StandardEventFactory = EventFactoryImpl(gameSession)

  def InfoEvent(story: EventStory, condition: EventPrecondition): Event =
    Event(Scenario(story), condition)

  class EventFactoryImpl(gameSession: GameSession) extends StandardEventFactory:
    val logger: Logger = LoggerFactory.getLogger(this.getClass)
    private val gameTurn = gameSession.gameTurn
    private val dice = gameSession.dice

    override def ImprisonEvent(blockingTime: Int): Event =
      val story: EventStory = EventStory(s"You are imprisoned for $blockingTime turns", Seq("Wait liberation"))
      val imprisonStrategy: Int => Unit = playerId =>
        gameTurn.getRemainingBlockedMovements(playerId) match
          case None =>
            gameTurn.lockPlayer(playerId, blockingTime)
          case _ =>
      Event(Scenario(imprisonStrategy, story))

    override def EscapeEvent(): Event =
      val escapeStrategy: Int => Unit = playerId =>
        if dice.rollOneDice() == dice.rollOneDice() then
          gameTurn.liberatePlayer(playerId)
          logger.info(s"Player $playerId is escaped from the jail")
          gameSession.setPlayerPosition(playerId, dice.rollMoreDice(2), true)
        else logger.info(s"Player $playerId fails to escape from the jail")
      val escapeStory: EventStory = EventStory(s"You have an opportunity to escape", Seq("Try to escape"))
      val escapePrecondition: EventPrecondition = gameTurn.getRemainingBlockedMovements(_).nonEmpty
      Event(Scenario(escapeStrategy, escapeStory), escapePrecondition)
