package example

import example.controller.*

import scala.collection.mutable.ListBuffer

object Main extends App:
  val gameSession = GameSessionInitializer.createDefaultGameSession(2)
  val terrains = TerrainInitializer.insertGameTerrains(gameSession)
  gameSession.gameStore.terrainList ++= terrains
  gameSession.startGame()
  GameEngineImpl(gameSession).start()
