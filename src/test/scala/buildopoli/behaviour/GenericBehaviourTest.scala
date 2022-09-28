package buildopoli.behaviour

import buildopoli.behaviour.BehaviourModule.Behaviour
import buildopoli.behaviour.event.story.EventStoryModule.EventStory
import buildopoli.behaviour.factory.{BehaviourFactory, EventFactory}
import buildopoli.util.GameSessionHelper.DefaultGameSession
import org.scalatest.funsuite.AnyFunSuite

class GenericBehaviourTest extends AnyFunSuite:
  private val PLAYER_1 = 1
  private val gameSession = DefaultGameSession(1)
  private val bFactory = BehaviourFactory(gameSession)
  private val eFactory = factory.EventFactory(gameSession)
  private val AMOUNT = 100

  test("Combination of two behaviours iterators") {
    val story = EventStory("Description", "Ok")
    val withdrawMoneyBehaviour = Behaviour(eFactory.WithdrawMoneyEvent(story, AMOUNT))
    val jailBehaviour = bFactory.JailBehaviour()

    val withdrawMoneyExplorer = withdrawMoneyBehaviour.getBehaviourExplorer(PLAYER_1)
    val jailExplorer = jailBehaviour.getBehaviourExplorer(PLAYER_1)
    assert(withdrawMoneyExplorer.currentEvents.length == 1)
    assert(jailExplorer.currentEvents.length == 1)

    val combinedExplorer = Behaviour.combineExplorers(withdrawMoneyBehaviour, jailBehaviour, PLAYER_1)

    assert(combinedExplorer.currentEvents.length == 2)
    assert(combinedExplorer.currentEvents.head == withdrawMoneyExplorer.currentEvents.head)
    assert(combinedExplorer.currentEvents.last == jailExplorer.currentEvents.head)
  }
