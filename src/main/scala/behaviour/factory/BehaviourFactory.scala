package behaviour.factory

import behaviour.BehaviourModule.*
import behaviour.event.EventFactory
import behaviour.event.EventModule.*
import behaviour.event.EventStoryModule.EventStory
import gameManagement.gameSession.GameSession
import gameManagement.gameTurn.GameTurn
import behaviour.event.EventFactory
import behaviour.event.*
import scala.util
import scala.util.Random

object BehaviourFactory:

  def apply(gameSession: GameSession): StandardBehaviourFactory = BehaviourFactoryImpl(gameSession)

  class BehaviourFactoryImpl(gameSession: GameSession) extends StandardBehaviourFactory:
    private val eventFactory = EventFactory(gameSession)

    override def JailBehaviour(blockingTime: Int): Behaviour =
      val imprisonEvent = eventFactory.ImprisonEvent(blockingTime)
      val escapeEvent = eventFactory.EscapeEvent()
      Behaviour(Seq(EventGroup(imprisonEvent, escapeEvent)))
