package gameManagement.gameStore

import player.Player
import terrain.Terrain

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

trait GameStore:
  var playersList: List[Player]
  def terrainList: ListBuffer[Terrain]

  var playerIdsCounter: Int = 0
  def getPlayer(playerId: Int): Player
  def getTerrain(position: Int): Terrain
  def putTerrain(terrain: Terrain*): Unit
  def addPlayer(player: Player): Unit

object GameStore:
  def apply(): GameStore =
    GameStoreImpl()