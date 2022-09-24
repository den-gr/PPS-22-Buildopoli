package example.controller

import lib.behaviour.BehaviourModule.Behaviour
import lib.behaviour.event.EventGroup
import lib.behaviour.factory
import lib.behaviour.factory.EventFactory
import lib.gameManagement.gameSession.GameSession

trait GlobalBehaviourInitializer:

  def buildGlobalBehaviour(): Behaviour

object GlobalBehaviourInitializer:
  def apply(gameSession: GameSession): GlobalBehaviourInitializer = GlobalBehaviourInitializerImpl(gameSession)

  private class GlobalBehaviourInitializerImpl(gameSession: GameSession) extends GlobalBehaviourInitializer:
    private val factory = EventFactory(gameSession)
    override def buildGlobalBehaviour(): Behaviour =
      val buildTokenEvent = factory.BuildTokenEvent(
        "Terrain where you can build",
        "Select type of building",
        "Select number of building",
        "Not enough money for"
      )
      val globalMortgageEvent = factory.MortgageEvent("You can mortgage your terrain and receive money")
      val globalRetrieveMortgageEvent = factory.RetrieveFromMortgageEvent(
        "You can retrieve your terrain from mortgage",
        "You have not enough money to retrieve the terrain"
      )
      Behaviour(
        EventGroup(
          Seq(buildTokenEvent, globalMortgageEvent, globalRetrieveMortgageEvent),
          isAtomic = true
        )
      )
