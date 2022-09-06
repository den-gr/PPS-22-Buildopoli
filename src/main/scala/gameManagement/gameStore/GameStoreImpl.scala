package gameManagement.gameStore

import player.Player
import terrain.Terrain

import scala.collection.mutable.ListBuffer

case class GameStoreImpl() extends GameStore:

  override val terrainList: ListBuffer[Terrain] = ListBuffer()
  private var listOfPLayer: List[Player] = List()
  private var gameStarted: Boolean = false

  def playersList: List[Player] = listOfPLayer
  def playersList_=(list: List[Player]): Unit = this.listOfPLayer = list

  override def getPlayer(playerId: Int): Player = playersList.find(p => p.playerId.equals(playerId)).get
  override def getTerrain(position: Int): Terrain = terrainList
    .filter(t => t.basicInfo.position.equals(position))
    .result()
    .head

  override def startGame(): Unit = this.gameStarted = true
  def checkGameStarted(): Unit = if gameStarted then throw new InterruptedException("Game already started !")
  override def addPlayer(player: Player): Unit =
    checkGameStarted()
    playersList = playersList :+ player
  override def putTerrain(terrain: Terrain*): Unit =
    checkGameStarted()
    terrainList ++= terrain
