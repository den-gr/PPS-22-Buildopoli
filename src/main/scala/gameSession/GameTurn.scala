package gameSession

import player.Player
import scala.collection.mutable.ListBuffer

trait GameTurn:
  var playerWithTurn: ListBuffer[Int] = ListBuffer()
  var blockingList: Map[Int, Int] = Map()

  def selectNextPlayer(): Int
  def playerHasDoneTheActualTurn(playerId: Int): Boolean
  def lockPlayer(playerId: Int, turns: Int): Unit
  def liberatePlayer(playerId: Int): Unit
  def doTurn(): Unit
  def getRemainingBlockedMovements(playerId: Int): Option[Int]

case class DefaultGameTurn(jailBlockingTime: Int, playersList: ListBuffer[Player], selector: (ListBuffer[Player], ListBuffer[Int]) => Int) extends GameTurn:
  override def selectNextPlayer(): Int =
    val selection = selector.apply(playersList, playerWithTurn)
    playerWithTurn += selection
    everyoneHasDoneOneTurn()
    selection

  override def playerHasDoneTheActualTurn(playerId: Int): Boolean = playerWithTurn.contains(playerId)

  def everyoneHasDoneOneTurn(): Unit =
    if playerWithTurn.size == (playersList.size - blockingList.size) then
      playerWithTurn.clear()
      doTurn()

  override def lockPlayer(playerId: Int, turns: Int): Unit =
    blockingList = blockingList + (playerId -> turns)

  override def liberatePlayer(playerId: Int): Unit =
    blockingList = blockingList - playerId

  override def doTurn(): Unit =
    if blockingList.nonEmpty then
      blockingList.toList.foreach(_ match
        case (key: Int, value: Int) if value >= 0 => blockingList = blockingList + (key -> (value - 1))
      )
      blockingList = blockingList.filter(_._2 > 0)

  override def getRemainingBlockedMovements(playerId: Int): Option[Int] =
    blockingList.get(playerId)