package behaviour.factory

import behaviour.BehaviourModule.*
import behaviour.event.EventModule.*
import behaviour.event.EventStoryModule.EventStory
import gameManagement.gameSession.GameSession
import gameManagement.gameTurn.GameTurn

import scala.util
import scala.util.Random

object BehaviourFactory:

  def apply(gameSession: GameSession): StandardBehaviourFactory = BehaviourFactoryImpl(gameSession)

  class BehaviourFactoryImpl(gameSession: GameSession) extends StandardBehaviourFactory:
    private val gameTurn = gameSession.gameTurn
    private val dice = gameSession.dice
    override def JailBehaviour(blockingTime: Int): Behaviour =
      val story: EventStory = EventStory(s"You are imprisoned for $blockingTime turns", Seq("Wait liberation"))
      val imprisonStrategy: Int => Unit = playerId =>
        gameTurn.getRemainingBlockedMovements(playerId) match
          case None =>
            gameTurn.lockPlayer(playerId, blockingTime)
          case _ =>
      val imprisonEvent: Event = Event(Scenario(imprisonStrategy, story))
      val escapeStrategy: Int => Unit = playerId =>
        if dice.rollOneDice() == dice.rollOneDice() then gameTurn.liberatePlayer(playerId)
        gameSession.setPlayerPosition(playerId, dice.rollMoreDice(2), true)
      // TODO logger(User is escaped)
      // else
      // TODO logger(fail to escape)
      val escapeStory: EventStory = EventStory(s"You have an opportunity to escape", Seq("Try to escape"))
      val escapePrecondition: EventPrecondition = gameTurn.getRemainingBlockedMovements(_).nonEmpty
      val escapeEvent: Event = Event(Scenario(escapeStrategy, escapeStory), escapePrecondition)
      Behaviour(Seq(EventGroup(imprisonEvent, escapeEvent)))
