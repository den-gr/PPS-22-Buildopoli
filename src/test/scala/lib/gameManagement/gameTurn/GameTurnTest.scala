package lib.gameManagement.gameTurn

import lib.gameManagement.gameOptions.GameOptions
import lib.gameManagement.gameStore.GameStore
import lib.gameManagement.gameTurn.GameTurn
import lib.player.{Player, PlayerImpl}
import org.scalatest.BeforeAndAfterEach
import org.scalatest.funsuite.AnyFunSuite

import scala.collection.mutable.ListBuffer

class GameTurnTest extends AnyFunSuite with BeforeAndAfterEach:

  var gameStore: GameStore = _
  var gameOptions: GameOptions = _
  var gameTurn: DefaultGameTurn = _
  override def beforeEach(): Unit =
    val selector: (Seq[Player], Seq[Int]) => Int =
      (playerList: Seq[Player], playerWithTurn: Seq[Int]) =>
        playerList.filter(el => !playerWithTurn.contains(el.playerId)).head.playerId
    gameStore = GameStore()
    gameOptions = GameOptions(200, 2, 10, 6, selector)
    gameTurn = DefaultGameTurn(gameOptions, gameStore)
    for _ <- 0 until 3 do gameStore.addPlayer()

  test("Verifying that each player is doing one turn, so the list with players that have done the turn is empty") {
    gameStore.playersList.foreach(_ => gameTurn.selectNextPlayer())
    assert(gameTurn.playerWithTurn.size == gameStore.playersList.size)

    gameTurn.selectNextPlayer()
    assert(gameTurn.playerWithTurn.size == 1)
  }

  test(
    "Verifying that each player (except the one blocked in prison) is doing one turn, " +
      "so the list with player with turn is empty and the locked player has one less turn to be stopped"
  ) {
    gameTurn.lockPlayer(gameStore.playersList.head.playerId, 2)
    for _ <- 0 until 2 do gameTurn.selectNextPlayer()

    assert(gameTurn.playerWithTurn.size == 2)
    gameTurn.selectNextPlayer()
    assert(gameTurn.playerWithTurn.size == 1)
    assert(gameTurn.getRemainingBlockedMovements(gameStore.playersList.head.playerId).get == 1)
  }

  test("Free players, doing another turn, liberate the locked player") {
    gameTurn.lockPlayer(gameStore.playersList.head.playerId, 2)
    for _ <- 0 until 3 do gameTurn.selectNextPlayer()

    assert(gameTurn.playerWithTurn.size == 1)
    assert(gameTurn.getRemainingBlockedMovements(gameStore.playersList.head.playerId).get == 1)
    for _ <- 0 until 3 do gameTurn.selectNextPlayer()
    assert(gameTurn.blockingList.isEmpty)
  }

  test("liberate player from prison, before time has finished") {
    gameTurn.lockPlayer(gameStore.playersList.head.playerId, 2)

    for _ <- 0 until 2 do gameTurn.selectNextPlayer()
    assert(gameTurn.playerWithTurn.size == 2)

    gameTurn.liberatePlayer(gameStore.playersList.head.playerId)
    assert(gameTurn.blockingList.isEmpty)

    for _ <- 0 until 1 do gameTurn.selectNextPlayer()
    assert(gameTurn.playerWithTurn.size == 3)

    for _ <- 0 until 2 do gameTurn.selectNextPlayer()
    assert(gameTurn.playerWithTurn.size == 2)
  }
