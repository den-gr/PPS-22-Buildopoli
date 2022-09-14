package lib.gameManagement.gameStore

import lib.gameManagement.gameStore.gameInputs.{GameInputs, UserInputs}
import lib.player.Player
import lib.terrain.Terrain

import scala.collection.mutable.ListBuffer

case class GameStoreImpl() extends GameStore:

  private var listOfTerrains: Seq[Terrain] = List()
  
  override val userInputs: GameInputs = UserInputs()

  private var listOfPLayer: Seq[Player] = List()

  private var gameStarted: Boolean = false
  
  def playersList: Seq[Player] = listOfPLayer
  def playersList_=(list: Seq[Player]): Unit = this.listOfPLayer = list
  
  def terrainList: Seq[Terrain] = listOfTerrains
  def terrainList_=(list: Seq[Terrain]): Unit = this.listOfTerrains = list

  override def getPlayer(playerId: Int): Player = playersList.find(p => p.playerId.equals(playerId)).get

  override def addPlayer(player: Player): Unit =
    checkGameStarted()
    playersList = playersList :+ player

  override def getTerrain(position: Int): Terrain = terrainList
    .filter(t => t.basicInfo.position.equals(position)).head

  override def putTerrain(terrain: Terrain*): Unit =
    checkGameStarted()
    terrainList ++= terrain

  override def startGame(): Unit = this.gameStarted = true
  def checkGameStarted(): Unit = if gameStarted then throw new InterruptedException("Game already started !")
