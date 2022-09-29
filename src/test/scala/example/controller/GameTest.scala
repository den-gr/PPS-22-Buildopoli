package example.controller

import example.view.{GameView, PlayerChoice, View}
import buildopoli.behaviour.event.story.EventStoryModule.StoryGroup
import buildopoli.gameManagement.gameSession.GameSession
import org.scalatest.{BeforeAndAfterEach, GivenWhenThen}
import org.scalatest.featurespec.AnyFeatureSpec
import org.scalatest.funsuite.AnyFunSuite

import scala.util.Random

/** Test game example
  */
class GameTest extends AnyFunSuite with BeforeAndAfterEach:
  private val MAX_MOVES = 1000
  private var mockView: View = _
  private var gameSession: GameSession = _

  override def beforeEach(): Unit =
    mockView = new GameView: // Custom view that can autonomously play the game
      override def getUserChoices(stories: Seq[StoryGroup]): PlayerChoice =
        if Random.nextBoolean() then
          val groupIndex = Random.nextInt(stories.length)
          val storyIndex = Random.nextInt(stories(groupIndex).length)
          val choiceIndex = Random.nextInt(stories(groupIndex)(storyIndex).choices.length)
          println(s"Choice: ($groupIndex, $storyIndex, $choiceIndex)")
          PlayerChoice.Choice(groupIndex, storyIndex, choiceIndex)
        else
          println("Skip")
          PlayerChoice.EndTurn

    gameSession = GameSessionInitializer.createDefaultGameSession(3)
    val terrains = TerrainInitializer(gameSession).buildGameTerrains()
    gameSession.gameStore.terrainList ++= terrains
    gameSession.gameStore.globalBehaviour = GlobalBehaviourInitializer(gameSession).buildGlobalBehaviour()

  /** This test is not stable. Can be used only for very coarse verifications
    */
  ignore(s"Game not throw exception if it is played randomly (max $MAX_MOVES moves)") {
    val controller = GameControllerImpl(gameSession, mockView, MAX_MOVES)
    assert({ controller.start(); true })
  }

  test("Cannot run the game twice") {
    val controller = GameControllerImpl(gameSession, mockView, maxMoves = 1)
    controller.start()
    assertThrows[IllegalStateException](controller.start())
  }
