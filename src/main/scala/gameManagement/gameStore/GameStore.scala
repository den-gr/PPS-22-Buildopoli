package gameManagement.gameStore

import player.Player
import terrain.Terrain

import scala.collection.mutable.ListBuffer

trait GameStore:
  def playersList: ListBuffer[Player]
  def terrainList: ListBuffer[Terrain]
  var playerIdsCounter: Int = 0

  def getPlayer(playerId: Int): Player

object GameStore:
  def apply(): GameStore =
    GameStoreImpl()