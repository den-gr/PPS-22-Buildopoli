package lib.behaviour.factory

import lib.behaviour.BehaviourModule.*
import lib.behaviour.event.EventModule.*
import lib.behaviour.event.*
import lib.behaviour.event.EventFactory
import lib.behaviour.factory.input.JailBehaviourInput
import lib.gameManagement.gameSession.GameSession

import scala.util
import scala.util.Random

object BehaviourFactory:

  /** Constructor for a behaviour factory for a specific game session
    * @param gameSession
    *   current game session
    * @return
    *   factory that can produce behaviours
    */
  def apply(gameSession: GameSession): BasicBehaviourFactory = BehaviourFactoryImpl(gameSession)

  private class BehaviourFactoryImpl(gameSession: GameSession) extends BasicBehaviourFactory:
    private val eventFactory = EventFactory(gameSession)

    override def JailBehaviour(input: JailBehaviourInput): Behaviour =
      val imprisonEvent = eventFactory.ImprisonEvent(input.imprisonStory, input.blockingTurns)
      val escapeEvent = eventFactory.EscapeEvent(input.escapeStory, input.escapeSuccessMsg, input.escapeFailMsg)
      Behaviour(imprisonEvent, escapeEvent)
