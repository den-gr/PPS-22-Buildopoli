package gameBank

import gameOptions.GameOptions
import gameSession.{GameSession, GameSessionImpl}
import lap.Lap.{GameLap, MoneyReward}
import org.scalatest.funsuite.AnyFunSuite
import player.Player

import scala.collection.mutable.ListBuffer

class GameBankTest extends AnyFunSuite:

  val selector: (ListBuffer[Player], ListBuffer[Int]) => Int = (playerList: ListBuffer[Player], playerWithTurn: ListBuffer[Int]) =>
    val tempList = playerList.filter(el => !playerWithTurn.contains(el.playerId))
    tempList.head.playerId
  val gameSession: GameSession = GameSessionImpl(GameOptions(0, 2, true, 10, MoneyReward(200), selector), GameLap())
  var playerCounter = 0

  test("player has incremented money") {
    addPlayer(1)
    gameSession.gameBank.increasePlayerMoney(1, 1000)
    assert(gameSession.gameBank.getMoneyForPlayer(1) === 1000)
  }

  test("player has decremented money") {
    gameSession.gameBank.decreasePlayerMoney(1, 100)
    assert(gameSession.gameBank.getMoneyForPlayer(1) === 900)
  }

  test("two players make a transaction") {
    addPlayer(2)
    gameSession.gameBank.increasePlayerMoney(2, 500)
    gameSession.gameBank.makeTransaction(2, 1, 300)
    assert(gameSession.gameBank.getMoneyForPlayer(2) === 200)
    assert(gameSession.gameBank.getMoneyForPlayer(1) === 1200)
  }

  test("player has debit (and zero money) after decreasing money") {
    addPlayer(3)
    gameSession.gameBank.increasePlayerMoney(3, 500)
    gameSession.gameBank.decreasePlayerMoney(3, 600)
    assert(gameSession.gameBank.getDebtsForPlayer(3) === 100)
    assert(gameSession.gameBank.getMoneyForPlayer(3) === 0)
  }

  test("player has decreased debit after money increase") {
    assert(gameSession.gameBank.getDebtsForPlayer(3) === 100)
    gameSession.gameBank.increasePlayerMoney(3, 50)
    assert(gameSession.gameBank.getDebtsForPlayer(3) === 50)
  }

  test("player has increased debit after money decrease") {
    assert(gameSession.gameBank.getDebtsForPlayer(3) === 50)
    gameSession.gameBank.decreasePlayerMoney(3, 150)
    assert(gameSession.gameBank.getDebtsForPlayer(3) === 200)
  }

  test("player has zero debit") {
    assert(gameSession.gameBank.getDebtsForPlayer(3) === 200)
    gameSession.gameBank.increasePlayerMoney(3, 250)
    assert(gameSession.gameBank.getDebtsForPlayer(3) === 0)
    assert(gameSession.gameBank.getMoneyForPlayer(3) === 50)
  }

  test("initial player has debit 0") {
    addPlayer(4)
    assert(gameSession.gameBank.getDebtsForPlayer(4) === 0)
  }

  test("throw exception when debit not enabled and player does not have enough money") {
    val gameSession: GameSession = GameSessionImpl(GameOptions(0, 2, false, 10, MoneyReward(200), selector), GameLap())
    gameSession.addOnePlayer(Option.apply(1))
    gameSession.addOnePlayer(Option.apply(2))
    assert(gameSession.getPlayersList.size === 2)
    gameSession.gameBank.increasePlayerMoney(1, 100)
    gameSession.gameBank.increasePlayerMoney(2, 400)
    assert(gameSession.gameBank.getMoneyForPlayer(1) === 100)
    assert(gameSession.gameBank.getMoneyForPlayer(2) === 400)
    assertThrows[IllegalStateException](gameSession.gameBank.makeTransaction(1, 2, 200))
  }

  def addPlayer(id: Int): Unit =
    gameSession.addOnePlayer(Option.apply(id))
    playerCounter += 1
    assert(gameSession.getPlayersList.size === playerCounter)
