package gameManagement.gameTurn

import gameManagement.gameOptions.GameOptions
import gameManagement.gameStore.GameStore
import player.Player

import scala.collection.mutable.ListBuffer

case class DefaultGameTurn(gameOptions: GameOptions, gameStore: GameStore) extends GameTurn:
  override def selectNextPlayer(): Int =
    val selection = gameOptions.playerTurnSelector.apply(gameStore.playersList, playerWithTurn)
    playerWithTurn += selection
    everyoneHasDoneOneTurn()
    selection

  override def playerHasDoneTheActualTurn(playerId: Int): Boolean = playerWithTurn.contains(playerId)

  def everyoneHasDoneOneTurn(): Unit =
    if playerWithTurn.size == (gameStore.playersList.size - blockingList.size) then
      playerWithTurn.clear()
      doTurn()

  override def lockPlayer(playerId: Int): Unit =
    blockingList = blockingList + (playerId -> gameOptions.jailBlockingTime)

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