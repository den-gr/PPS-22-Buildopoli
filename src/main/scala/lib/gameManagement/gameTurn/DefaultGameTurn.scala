package lib.gameManagement.gameTurn

import lib.gameManagement.gameOptions.GameOptions
import lib.gameManagement.gameStore.GameStore
import lib.player.Player

import scala.collection.mutable.ListBuffer

/** Basic (possible) implementation of GameTurn
  * @param gameOptions
  *   to access selector Lambda that selects the next player
  * @param gameStore
  *   to access playersList in the game
  */
case class DefaultGameTurn(gameOptions: GameOptions, gameStore: GameStore) extends GameTurn:
  override def selectNextPlayer(): Int =
    if !isNextTurnOpen then throw new RuntimeException("Previous player input values not emptied")
    val selection: Int = gameOptions.playerTurnSelector.apply(gameStore.playersList, playerWithTurn)
    playerWithTurn = playerWithTurn.::(selection)
    everyoneHasDoneOneTurn()
    selection

  override protected def isNextTurnOpen: Boolean = gameStore.userInputs.isListEmpty

  override def playerHasDoneTheActualTurn(playerId: Int): Boolean = playerWithTurn.contains(playerId)

  def everyoneHasDoneOneTurn(): Unit =
    if playerWithTurn.size == (gameStore.playersList.size - blockingList.size) then
      playerWithTurn = List()
      doTurn()

  override def lockPlayer(playerId: Int, blockingTime: Int): Unit =
    blockingList = blockingList + (playerId -> blockingTime)

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
