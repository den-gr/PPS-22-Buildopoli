package gameManagement.gameTurn

import gameManagement.gameOptions.GameOptions
import gameManagement.gameStore.{GameStore, GameStoreImpl}
import gameManagement.gameTurn.{DefaultGameTurn, GameTurn}
import org.scalatest.funsuite.AnyFunSuite
import player.{Player, PlayerImpl}

import scala.collection.mutable.ListBuffer

class GameTurnTest extends AnyFunSuite:

  val selector: (Seq[Player], Seq[Int]) => Int =
    (playerList: Seq[Player], playerWithTurn: Seq[Int]) =>
      playerList.filter(el => !playerWithTurn.contains(el.playerId)).head.playerId

  val gameStore: GameStore = GameStoreImpl()
  val gameOptions: GameOptions = GameOptions(200, 2, 10, 6, selector)
  val gameTurn: DefaultGameTurn = DefaultGameTurn(gameOptions, gameStore)

  test("Verifying that each player is doing one turn, so the list with players that have done the turn is empty") {
    for i <- 1 until 6 do
      gameStore.addPlayer(PlayerImpl(i))

    for _ <- 1 until 6 do
      gameTurn.selectNextPlayer()

    assert(gameTurn.playerWithTurn.isEmpty)
  }

  test("Verifying that each player (except the one blocked in prison) is doing one turn, " +
    "so the list with player with turn is empty and the locked player has one less turn to be stopped") {
    gameTurn.lockPlayer(gameStore.playersList.head.playerId, 2)
    for _ <- 1 until 5 do
      gameTurn.selectNextPlayer()

    assert(gameTurn.playerWithTurn.isEmpty)
    assert(gameTurn.getRemainingBlockedMovements(gameStore.playersList.head.playerId).get == 1)
  }

  test("Free players, doing another turn, liberate the locked player") {
    for _ <- 1 until 5 do
      gameTurn.selectNextPlayer()

    assert(gameTurn.playerWithTurn.isEmpty)
    assert(gameTurn.getRemainingBlockedMovements(gameStore.playersList.head.playerId).isEmpty)
    assert(gameTurn.blockingList.isEmpty)
  }

  test("liberate player from prison, before time has finished") {
    for _ <- 0 until 2 do
      gameTurn.selectNextPlayer()
    assert(gameTurn.playerWithTurn.size == 2)

    gameTurn.liberatePlayer(gameStore.playersList.head.playerId)
    assert(gameTurn.blockingList.isEmpty)

    for _ <- 0 until 2 do
      gameTurn.selectNextPlayer()
    assert(gameTurn.playerWithTurn.size == 4)

    for _ <- 0 until 2 do
      gameTurn.selectNextPlayer()
    assert(gameTurn.playerWithTurn.size == 1)
  }

