package lib.gameManagement.gameStore

import lib.gameManagement.gameStore.gameInputs.GameInputs
import lib.player.Player
import lib.terrain.{Purchasable, Terrain}

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

/** Represents some form of storage for the game. Contains list of players and list of terrains, plus some useful
  * methods to manage those lists.
  */
trait GameStore:
  /** Immutable lists of players in the game
    */
  var playersList: Seq[Player]

  /** @return
    *   Mutable list of terrains in the game
    */
  var terrainList: Seq[Terrain]

  /** Used to assing an incremental id to players being created
    */
  var playerIdsCounter: Int = 0

  def userInputs: GameInputs

  /** @param playerId
    *   identifying one player
    * @return
    *   the player object
    */
  def getPlayer(playerId: Int): Player

  /** @param player
    *   to put into playersList
    */
  def addPlayer(player: Player): Unit

  /** @param position
    *   identifying one terrain
    * @return
    *   the terrain object
    */
  def getTerrain(position: Int): Terrain

  /** @param terrain
    *   to put into the list
    */
  def putTerrain(terrain: Terrain*): Unit

  /** @return
    *   actual number of terrains into the game
    */
  def getNumberOfTerrains(predicate: Terrain => Boolean): Int = terrainList.count(predicate)
  def getTypeOfTerrains(predicate: Terrain => Boolean): Seq[Terrain] = terrainList.filter(predicate)

  def startGame(): Unit

object GameStore:
  def apply(): GameStore =
    GameStoreImpl()
