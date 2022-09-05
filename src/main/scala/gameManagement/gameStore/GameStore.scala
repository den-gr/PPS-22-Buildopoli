package gameManagement.gameStore

import player.Player
import terrain.Terrain.Terrain

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

/**
 * Represents some form of storage for the game.
 * Contains list of players and list of terrains, plus some useful methods to manage those lists.
 */
trait GameStore:
  /**
   * Immutable lists of players in the game
   */
  var playersList: List[Player]

  /**
   * @return Mutable list of terrains in the game
   */
  def terrainList: ListBuffer[Terrain]

  /**
   * Used to assing an incremental id to players being created
   */
  var playerIdsCounter: Int = 0

  /**
   * @param playerId identifying one player
   * @return the player object
   */
  def getPlayer(playerId: Int): Player

  /**
   *
   * @param position identifying one terrain
   * @return the terrain object
   */
  def getTerrain(position: Int): Terrain

  /**
   * @param terrain to put into the list
   */
  def putTerrain(terrain: Terrain*): Unit

  /**
   * @param player to put into playersList
   */
  def addPlayer(player: Player): Unit

object GameStore:
  def apply(): GameStore =
    GameStoreImpl()