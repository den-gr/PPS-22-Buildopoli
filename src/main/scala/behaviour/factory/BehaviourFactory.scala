package behaviour.factory

import behaviour.BehaviourModule.*
import behaviour.event.EventFactory
import behaviour.event.EventModule.*
import gameManagement.gameSession.GameSession
import behaviour.event.EventFactory
import behaviour.event.*
import behaviour.factory.input.JailBehaviourInput

import scala.util
import scala.util.Random

object BehaviourFactory:

  def apply(gameSession: GameSession): StandardBehaviourFactory = BehaviourFactoryImpl(gameSession)

  class BehaviourFactoryImpl(gameSession: GameSession) extends StandardBehaviourFactory:
    private val eventFactory = EventFactory(gameSession)

    override def JailBehaviour(input: JailBehaviourInput): Behaviour =

      val imprisonEvent = eventFactory.ImprisonEvent(input.imprisonStory, input.blockingTime)
      val escapeEvent = eventFactory.EscapeEvent(input.escapeStory, input.escapeSuccessMsg, input.escapeFailMsg)
      Behaviour(Seq(EventGroup(imprisonEvent, escapeEvent)))
