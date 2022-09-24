package lib.behaviour.factory

import lib.behaviour.BehaviourModule.*
import lib.behaviour.event.EventModule.*
import lib.behaviour.event.*
import lib.behaviour.event.story.EventStoryModule
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
      Behaviour(EventGroup(Seq(imprisonEvent, escapeEvent), isMandatory = true))

    override def PurchasableTerrainBehaviour(
        payRentStory: EventStoryModule.EventStory,
        notMoneyErrMsg: String,
        buyTerrainStory: EventStoryModule.EventStory
    ): Behaviour =
      val buyTerrainEvent = eventFactory.BuyTerrainEvent(buyTerrainStory, notMoneyErrMsg)
      val rentTerrainEvent = eventFactory.GetRentEvent(payRentStory)
      Behaviour(Seq(EventGroup(buyTerrainEvent), EventGroup(Seq(rentTerrainEvent), isMandatory = true)))
