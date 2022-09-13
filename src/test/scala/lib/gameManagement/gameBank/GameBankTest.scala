package lib.gameManagement.gameBank

import lib.gameManagement.gameBank.{Bank, GameBankImpl}
import lib.gameManagement.gameOptions.GameOptions
import lib.gameManagement.gameStore.{GameStore, GameStoreImpl}
import lib.player.{Player, PlayerImpl}
import org.scalatest.funsuite.AnyFunSuite

import scala.collection.mutable.ListBuffer

class GameBankTest extends AnyFunSuite:

  val selector: (Seq[Player], Seq[Int]) => Int =
    (playerList: Seq[Player], playerWithTurn: Seq[Int]) =>
      playerList.filter(el => !playerWithTurn.contains(el.playerId)).head.playerId

  val gameStore: GameStore = GameStoreImpl()
  val gameOptions: GameOptions = GameOptions(0, 2, 10, 6, selector)
  val gameBank: Bank = GameBankImpl(gameOptions, gameStore)

  var playerCounter = 0

  test("player has incremented money") {
    addPlayer(1)
    gameBank.makeTransaction(receiverId = 1, 1000)
    assert(gameBank.getMoneyForPlayer(1) === 1000)
  }

  test("player has decremented money") {
    gameBank.makeTransaction(1, amount = 100)
    assert(gameBank.getMoneyForPlayer(1) === 900)
  }

  test("two players make a transaction") {
    addPlayer(2)
    gameBank.makeTransaction(receiverId = 2, 500)
    gameBank.makeTransaction(2, 1, 300)
    assert(gameBank.getMoneyForPlayer(2) === 200)
    assert(gameBank.getMoneyForPlayer(1) === 1200)
  }

  test("player has debit (and zero money) after decreasing money") {
    addPlayer(3)
    gameBank.makeTransaction(receiverId = 3, 500)
    gameBank.makeTransaction(3, amount = 600)
    assert(gameBank.getDebtsForPlayer(3) === 100)
    assert(gameBank.getMoneyForPlayer(3) === 0)
  }

  test("player has decreased debit after money increase") {
    assert(gameBank.getDebtsForPlayer(3) === 100)
    gameBank.makeTransaction(receiverId = 3, 50)
    assert(gameBank.getDebtsForPlayer(3) === 50)
  }

  test("player has increased debit after money decrease") {
    assert(gameBank.getDebtsForPlayer(3) === 50)
    gameBank.makeTransaction(3, amount = 150)
    assert(gameBank.getDebtsForPlayer(3) === 200)
  }

  test("player has zero debit") {
    assert(gameBank.getDebtsForPlayer(3) === 200)
    gameBank.makeTransaction(receiverId = 3, 250)
    assert(gameBank.getDebtsForPlayer(3) === 0)
    assert(gameBank.getMoneyForPlayer(3) === 50)
  }

  test("initial player has debit 0") {
    addPlayer(4)
    assert(gameBank.getDebtsForPlayer(4) === 0)
  }

  test("throw exception when debit not enabled and player does not have enough money") {
    val gameOptions: GameOptions = GameOptions(0, 2, 10, 6, selector)
    val gameBank: Bank = GameBankImpl(gameOptions, gameStore)

    addPlayer(5)
    addPlayer(6)

    assert(gameStore.playersList.size === playerCounter)
    gameBank.makeTransaction(receiverId = 5, 100)
    gameBank.makeTransaction(receiverId = 6, 400)
    assert(gameBank.getMoneyForPlayer(5) === 100)
    assert(gameBank.getMoneyForPlayer(6) === 400)
    assertThrows[IllegalStateException](gameBank.makeTransaction(5, 6, 200))
  }

  test("player with debit cannot make transaction") {
    val gameOptions: GameOptions = GameOptions(0, 2, 10, 6, selector)
    val gameBank: Bank = GameBankImpl(gameOptions, gameStore)

    addPlayer(7)
    addPlayer(8)

    gameBank.makeTransaction(receiverId = 7, 200)
    gameBank.makeTransaction(receiverId = 8, 1000)
    assert(gameBank.getMoneyForPlayer(7) === 200)
    assert(gameBank.getMoneyForPlayer(8) === 1000)
    gameBank.makeGlobalTransaction(receiverId = 8, 300)
    assert(gameBank.getMoneyForPlayer(7) === 0)
    assert(gameBank.getDebtsForPlayer(7) === 100)

    assertThrows[IllegalStateException](gameBank.makeTransaction(7, 8, 100))
  }

  def addPlayer(id: Int): Unit =
    gameStore.addPlayer(PlayerImpl(id))
    playerCounter += 1
    assert(gameStore.playersList.size === playerCounter)
