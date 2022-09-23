package example.controller

import example.view.{GameView, PlayerChoice, View}
import lib.behaviour.event.story.EventStoryModule.StoryGroup
import lib.gameManagement.gameSession.GameSession
import org.scalatest.{BeforeAndAfterEach, GivenWhenThen}
import org.scalatest.featurespec.AnyFeatureSpec
import org.scalatest.funsuite.AnyFunSuite

import scala.util.Random

class GameTest extends AnyFunSuite with BeforeAndAfterEach:
  private var mockView: View = _
  private var gameSession: GameSession = _

  override def beforeEach(): Unit =
    mockView = new GameView:
      override def getUserChoices(stories: Seq[StoryGroup]): PlayerChoice =
        if Math.random() > 0.5 then
          val groupIndex = Random.nextInt(stories.length)
          val storyIndex = Random.nextInt(stories(groupIndex).length)
          val choiceIndex = Random.nextInt(stories(groupIndex)(storyIndex).choices.length)
          PlayerChoice.Choice(groupIndex, storyIndex, choiceIndex)
        else PlayerChoice.EndTurn

    gameSession = GameSessionInitializer.createDefaultGameSession(3)
    val terrains = TerrainInitializer(gameSession).buildGameTerrains()
    gameSession.gameStore.terrainList ++= terrains
    gameSession.gameStore.globalBehaviour = GlobalBehaviourInitializer(gameSession).buildGlobalBehaviour()

  test("Game not throw exception if it is played randomly 1000 moves") {
    val controller = GameControllerImpl(gameSession, mockView, maxMoves = 1000)
    assert({ controller.start(); true })
  }

  test("Cannot run a game twice") {
    val controller = GameControllerImpl(gameSession, mockView, maxMoves = 10)
    controller.start()
    assertThrows[IllegalStateException](controller.start())
  }
