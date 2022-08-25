package gameManagement.gameStore

import player.Player
import terrain.Terrain.Terrain

import scala.collection.mutable.ListBuffer

case class GameStoreImpl() extends GameStore:
  override val playersList: ListBuffer[Player] = ListBuffer()
  override val terrainList: ListBuffer[Terrain] = ListBuffer()

  override def getPlayer(playerId: Int): Player = playersList
    .filter(p => p.playerId.equals(playerId))
    .result()
    .head
