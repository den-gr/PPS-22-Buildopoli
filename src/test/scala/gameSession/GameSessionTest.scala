package gameSession

import gameOptions.GameOptions
import gameSession.{GameSession, GameSessionImpl}
import lap.Lap.{GameLap, Reward}
import org.scalatest.funsuite.AnyFunSuite

class GameSessionTest extends AnyFunSuite:
  val initialMoney = 100
  val gameSession: GameSession = GameSessionImpl(GameOptions(initialMoney, 2, true, 10), GameLap())

  test("playersList has initial size at zero") {
    assert(gameSession.getPlayersList.size === 0)
  }

  test("playerList size increased after adding one element") {
    val previousSize: Int = gameSession.getPlayersList.size
    gameSession.addOnePlayer(Option.empty)
    assert(gameSession.getPlayersList.size === (previousSize + 1))
    assert(gameSession.gameBank.playersList.size === (previousSize + 1))
  }

  test("playerList size increased after adding multiple elements") {
    val previousSize: Int = gameSession.getPlayersList.size
    gameSession.addManyPlayers(5)
    assert(gameSession.getPlayersList.size === (previousSize + 5))
  }

  test("last inserted player has money of 1000 after being created") {
    val previousSize: Int = gameSession.getPlayersList.size
    gameSession.addOnePlayer(Option.apply(15))
    assert(gameSession.getPlayersList.size === (previousSize + 1))
    assert(gameSession.getPlayersList.last.playerId === 15)
    assert(gameSession.getPlayersList.last.getPlayerMoney === initialMoney)
  }

  test("duplicate player ID existence") {
    val previousSize: Int = gameSession.getPlayersList.size
    gameSession.addOnePlayer(Option.apply(2))
    assert(gameSession.getPlayersList.size === (previousSize + 1))
  }
