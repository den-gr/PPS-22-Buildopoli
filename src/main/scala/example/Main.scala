package example

import example.controller.*
import example.view.GameView

import scala.collection.mutable.ListBuffer

object Main extends App:
  val gameSession = GameSessionInitializer.createDefaultGameSession(2)
  val terrains = TerrainInitializer.insertGameTerrains(gameSession)
  gameSession.gameStore.terrainList ++= terrains

  GameControllerImpl(gameSession, GameView()).start()
