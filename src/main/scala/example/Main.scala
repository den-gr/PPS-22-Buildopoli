package example

import example.controller.*
import example.view.GameView

import scala.collection.mutable.ListBuffer

object Main extends App:
  val gameSession = GameSessionInitializer.createDefaultGameSession(2)
  val terrains = TerrainInitializer(gameSession).buildGameTerrains()
  gameSession.gameStore.terrainList ++= terrains
  gameSession.gameStore.globalBehaviour = GlobalBehaviourInitializer(gameSession).buildGlobalBehaviour()

  GameControllerImpl(gameSession, GameView()).start()
