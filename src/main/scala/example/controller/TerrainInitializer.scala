package example.controller

import lib.behaviour.BehaviourModule.Behaviour
import lib.behaviour.event.EventFactory
import lib.behaviour.event.EventStoryModule.EventStory
import lib.gameManagement.gameSession.GameSession
import lib.terrain.{Terrain, TerrainInfo}

trait TerrainInitializer:
  def insertGameTerrains(gameSession: GameSession): Seq[Terrain]

object TerrainInitializer extends TerrainInitializer:

  def insertGameTerrains(gameSession: GameSession): Seq[Terrain] =
    var terrains: Seq[Terrain] = Seq()
    var position = 1
    terrains = terrains :+ createEmptyTerrain(position)
    position += 1
    terrains = terrains :+ createWithdrawMoneyTerrain(gameSession, position)(50)
    position += 1
    terrains = terrains :+ createWithdrawMoneyTerrain(gameSession, position)(100)
    terrains

  private def createWithdrawMoneyTerrain(gameSession: GameSession, position: Int)(amount: Int): Terrain =
    val eventFactory = EventFactory(gameSession)
    val story = EventStory(s"You spend $amount money on a party", "Oh, noo")
    val behaviour = Behaviour(eventFactory.WithdrawMoneyEvent(story, amount))
    Terrain(TerrainInfo("Party", position), behaviour)

  private def createEmptyTerrain(position: Int): Terrain =
    Terrain(TerrainInfo("Go", position), Behaviour())
