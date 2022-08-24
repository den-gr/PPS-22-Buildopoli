package gameManagement.gameTurn

import player.Player

import scala.collection.mutable.ListBuffer

trait GameTurn:
  var playerWithTurn: ListBuffer[Int] = ListBuffer()
  var blockingList: Map[Int, Int] = Map()

  def selectNextPlayer(): Int
  def playerHasDoneTheActualTurn(playerId: Int): Boolean
  def lockPlayer(playerId: Int): Unit
  def liberatePlayer(playerId: Int): Unit
  def doTurn(): Unit
  def getRemainingBlockedMovements(playerId: Int): Option[Int]

