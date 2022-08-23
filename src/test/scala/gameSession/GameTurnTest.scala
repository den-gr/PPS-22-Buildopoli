package gameSession

import lap.Lap.{GameLap, MoneyReward}
import gameOptions.GameOptions
import org.scalatest.funsuite.AnyFunSuite
import player.Player

import scala.collection.mutable.ListBuffer

class GameTurnTest extends AnyFunSuite:
  val selector: (ListBuffer[Player], ListBuffer[Int]) => Int = (playerList: ListBuffer[Player], playerWithTurn: ListBuffer[Int]) =>
    val tempList = playerList.filter(el => !playerWithTurn.contains(el.playerId))
    tempList.head.playerId
  val initialMoney = 100
  val initialCells = 2
  val debtsManagement = true
  val totalCells = 10
  val gameOptions: GameOptions =
    GameOptions(initialMoney, initialCells, debtsManagement, totalCells, MoneyReward(200), selector)

  val gameSession: GameSession = GameSessionImpl(gameOptions, GameLap())

  test("playerList size increased after adding multiple elements") {
    val previousSize: Int = gameSession.getPlayersList.size
    gameSession.addManyPlayers(5)
    assert(gameSession.getPlayersList.size == (previousSize + 5))
  }

  test("Verifying that each player is doing one turn, so the list with players that have done the turn is empty") {
    for _ <- 0 until 5 do
      gameSession.gameTurn.selectNextPlayer()

    assert(gameSession.gameTurn.playerWithTurn.isEmpty)
  }

  test("Verifying that each player (except the one blocked in prison) is doing one turn, " +
    "so the list with player with turn is empty and the locked player has one less turn to be stopped") {
    gameSession.gameTurn.lockPlayer(gameSession.getPlayersList.head.playerId, 2)
    for _ <- 0 until 4 do
      gameSession.gameTurn.selectNextPlayer()

    assert(gameSession.gameTurn.playerWithTurn.isEmpty)
    assert(gameSession.gameTurn.getRemainingBlockedMovements(gameSession.getPlayersList.head.playerId).get == 1)
  }

  test("Free players, doing another turn, liberate the locked player") {
    for _ <- 0 until 4 do
      gameSession.gameTurn.selectNextPlayer()

    assert(gameSession.gameTurn.playerWithTurn.isEmpty)
    assert(gameSession.gameTurn.getRemainingBlockedMovements(gameSession.getPlayersList.head.playerId).isEmpty)
    assert(gameSession.gameTurn.blockingList.isEmpty)
  }

  test("liberate player from prison, before time has finished") {
    for _ <- 0 until 2 do
      gameSession.gameTurn.selectNextPlayer()
    assert(gameSession.gameTurn.playerWithTurn.size == 2)

    gameSession.gameTurn.liberatePlayer(gameSession.getPlayersList.head.playerId)
    assert(gameSession.gameTurn.blockingList.isEmpty)

    for _ <- 0 until 2 do
      gameSession.gameTurn.selectNextPlayer()
    assert(gameSession.gameTurn.playerWithTurn.size == 4)

    for _ <- 0 until 2 do
      gameSession.gameTurn.selectNextPlayer()
    assert(gameSession.gameTurn.playerWithTurn.size == 1)
  }

