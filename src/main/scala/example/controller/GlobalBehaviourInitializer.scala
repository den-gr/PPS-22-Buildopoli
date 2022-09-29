package example.controller

import buildopoli.behaviour.BehaviourModule.Behaviour
import buildopoli.behaviour.event.EventGroup
import buildopoli.behaviour.factory
import buildopoli.behaviour.factory.EventFactory
import buildopoli.gameManagement.gameSession.GameSession

/** Create global game [[Behaviour]]
  */
trait GlobalBehaviourInitializer:
  /** @return
    *   a default global game behaviour
    */
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
