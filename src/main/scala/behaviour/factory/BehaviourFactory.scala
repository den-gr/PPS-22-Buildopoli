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

  /**
   * Constructor for a behaviour factory for a specific game session 
   * @param gameSession current game session
   * @return factory that can produce behaviours
   */
  def apply(gameSession: GameSession): BasicBehaviourFactory = BehaviourFactoryImpl(gameSession)

  private class BehaviourFactoryImpl(gameSession: GameSession) extends BasicBehaviourFactory:
    private val eventFactory = EventFactory(gameSession)

    override def JailBehaviour(input: JailBehaviourInput): Behaviour =
      val imprisonEvent = eventFactory.ImprisonEvent(input.imprisonStory, input.blockingTurns)
      val escapeEvent = eventFactory.EscapeEvent(input.escapeStory, input.escapeSuccessMsg, input.escapeFailMsg)
      Behaviour(imprisonEvent, escapeEvent)
