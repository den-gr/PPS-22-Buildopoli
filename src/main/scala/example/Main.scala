package example

import example.controller.*
import example.view.GameView

import scala.collection.mutable.ListBuffer

/** Run a simple game example
  */
object Main extends App:
  val NUMBER_OF_PLAYERS = 2
  val gameSession = GameSessionInitializer.createDefaultGameSession(NUMBER_OF_PLAYERS)
  val terrains = TerrainInitializer(gameSession).buildGameTerrains()
  gameSession.gameStore.terrainList ++= terrains
  gameSession.gameStore.globalBehaviour = GlobalBehaviourInitializer(gameSession).buildGlobalBehaviour()

  GameControllerImpl(gameSession, GameView()).start()
