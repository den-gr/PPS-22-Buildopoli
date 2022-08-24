package gameBank

import gameManagement.gameBank.{Bank, GameBankImpl}
import gameManagement.gameOptions.GameOptions
import gameManagement.gameStore.{GameStore, GameStoreImpl}
import org.scalatest.funsuite.AnyFunSuite
import player.{Player, PlayerImpl}

import scala.collection.mutable.ListBuffer

class GameBankTest extends AnyFunSuite:

  val selector: (ListBuffer[Player], ListBuffer[Int]) => Int =
    (playerList: ListBuffer[Player], playerWithTurn: ListBuffer[Int]) =>
      playerList.filter(el => !playerWithTurn.contains(el.playerId)).head.playerId
  
  val gameStore: GameStore = GameStoreImpl()
  val gameOptions: GameOptions = GameOptions(0, 2, true, 10, 2, selector)
  val gameBank: Bank = GameBankImpl(gameOptions, gameStore)

  var playerCounter = 0

  test("player has incremented money") {
    addPlayer(1)
    gameBank.increasePlayerMoney(1, 1000)
    assert(gameBank.getMoneyForPlayer(1) === 1000)
  }

  test("player has decremented money") {
    gameBank.decreasePlayerMoney(1, 100)
    assert(gameBank.getMoneyForPlayer(1) === 900)
  }

  test("two players make a transaction") {
    addPlayer(2)
    gameBank.increasePlayerMoney(2, 500)
    gameBank.makeTransaction(2, 1, 300)
    assert(gameBank.getMoneyForPlayer(2) === 200)
    assert(gameBank.getMoneyForPlayer(1) === 1200)
  }

  test("player has debit (and zero money) after decreasing money") {
    addPlayer(3)
    gameBank.increasePlayerMoney(3, 500)
    gameBank.decreasePlayerMoney(3, 600)
    assert(gameBank.getDebtsForPlayer(3) === 100)
    assert(gameBank.getMoneyForPlayer(3) === 0)
  }

  test("player has decreased debit after money increase") {
    assert(gameBank.getDebtsForPlayer(3) === 100)
    gameBank.increasePlayerMoney(3, 50)
    assert(gameBank.getDebtsForPlayer(3) === 50)
  }

  test("player has increased debit after money decrease") {
    assert(gameBank.getDebtsForPlayer(3) === 50)
    gameBank.decreasePlayerMoney(3, 150)
    assert(gameBank.getDebtsForPlayer(3) === 200)
  }

  test("player has zero debit") {
    assert(gameBank.getDebtsForPlayer(3) === 200)
    gameBank.increasePlayerMoney(3, 250)
    assert(gameBank.getDebtsForPlayer(3) === 0)
    assert(gameBank.getMoneyForPlayer(3) === 50)
  }

  test("initial player has debit 0") {
    addPlayer(4)
    assert(gameBank.getDebtsForPlayer(4) === 0)
  }

  test("throw exception when debit not enabled and player does not have enough money") {
    val gameOptions: GameOptions = GameOptions(0, 2, false, 10, 2, selector)
    val gameBank: Bank = GameBankImpl(gameOptions, gameStore)

    addPlayer(5)
    addPlayer(6)

    assert(gameStore.playersList.size === playerCounter)
    gameBank.increasePlayerMoney(5, 100)
    gameBank.increasePlayerMoney(6, 400)
    assert(gameBank.getMoneyForPlayer(5) === 100)
    assert(gameBank.getMoneyForPlayer(6) === 400)
    assertThrows[IllegalStateException](gameBank.makeTransaction(5, 6, 200))
  }

  def addPlayer(id: Int): Unit =
    gameStore.playersList += PlayerImpl(id)
    playerCounter += 1
    assert(gameStore.playersList.size === playerCounter)
