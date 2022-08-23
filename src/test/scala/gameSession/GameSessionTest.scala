package gameSession

import gameOptions.GameOptions
import gameSession.{GameSession, GameSessionImpl}
import lap.Lap.{GameLap, MoneyReward, Reward}
import org.scalatest.funsuite.AnyFunSuite
import player.Player

import scala.collection.mutable.ListBuffer

class GameSessionTest extends AnyFunSuite:
  val selector: (ListBuffer[Player], ListBuffer[Int]) => Int = (playerList: ListBuffer[Player], playerWithTurn: ListBuffer[Int]) =>
    val tempList = playerList.filter(el => !playerWithTurn.contains(el.playerId))
    tempList.head.playerId
  val initialMoney = 100
  val gameSession: GameSession = GameSessionImpl(GameOptions(initialMoney, 2, true, 10, MoneyReward(200), selector), GameLap())

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
