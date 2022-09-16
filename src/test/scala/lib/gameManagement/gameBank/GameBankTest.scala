package lib.gameManagement.gameBank

import lib.gameManagement.gameBank.{Bank, GameBankImpl}
import lib.gameManagement.gameOptions.GameOptions
import lib.gameManagement.gameStore.{GameStore, GameStoreImpl}
import lib.player.{Player, PlayerImpl}
import org.scalatest.BeforeAndAfterEach
import org.scalatest.funsuite.AnyFunSuite

import scala.collection.mutable.ListBuffer

class GameBankTest extends AnyFunSuite with BeforeAndAfterEach:

  var gameBank: Bank = _
  override def beforeEach(): Unit =
    val selector: (Seq[Player], Seq[Int]) => Int =
      (playerList: Seq[Player], playerWithTurn: Seq[Int]) =>
        playerList.filter(el => !playerWithTurn.contains(el.playerId)).head.playerId
    val gameStore: GameStore = GameStoreImpl()
    val gameOptions: GameOptions = GameOptions(0, 2, 10, 6, selector)
    gameBank = GameBankImpl(gameStore)
    gameStore.addPlayer()
    gameStore.addPlayer()
    gameStore.addPlayer()

  test("player has incremented money") {
    gameBank.makeTransaction(receiverId = 1, 1000)
    assert(gameBank.getMoneyForPlayer(1) === 1000)
  }

  test("player has decremented money") {
    gameBank.makeTransaction(receiverId = 1, amount = 1000)
    gameBank.makeTransaction(senderId = 1, amount = 100)
    assert(gameBank.getMoneyForPlayer(1) === 900)
  }

  test("two players make a transaction") {
    gameBank.makeTransaction(receiverId = 1, 1000)
    gameBank.makeTransaction(receiverId = 2, 500)
    gameBank.makeTransaction(2, 1, 300)
    assert(gameBank.getMoneyForPlayer(2) === 200)
    assert(gameBank.getMoneyForPlayer(1) === 1300)
  }

  test("player has debit (and zero money) after decreasing money") {
    gameBank.makeTransaction(receiverId = 1, 500)
    gameBank.makeTransaction(1, amount = 600)
    assert(gameBank.getDebitForPlayer(1) === 100)
    assert(gameBank.getMoneyForPlayer(1) === 0)
  }

  test("player has decreased debit after money increase") {
    gameBank.makeTransaction(receiverId = 1, 500)
    gameBank.makeTransaction(1, amount = 600)
    assert(gameBank.getDebitForPlayer(1) === 100)
    assert(gameBank.getMoneyForPlayer(1) === 0)
    gameBank.makeTransaction(receiverId = 1, 50)
    assert(gameBank.getDebitForPlayer(1) === 50)
  }

  test("player has increased debit after money decrease") {
    gameBank.makeTransaction(receiverId = 1, 500)
    gameBank.makeTransaction(1, amount = 600)
    assert(gameBank.getDebitForPlayer(1) === 100)
    assert(gameBank.getMoneyForPlayer(1) === 0)
    gameBank.makeTransaction(1, amount = 150)
    assert(gameBank.getDebitForPlayer(1) === 250)
  }

  test("player has zero debit") {
    gameBank.makeTransaction(receiverId = 1, 500)
    gameBank.makeTransaction(1, amount = 600)
    assert(gameBank.getDebitForPlayer(1) === 100)
    assert(gameBank.getMoneyForPlayer(1) === 0)
    gameBank.makeTransaction(receiverId = 1, 200)
    assert(gameBank.getDebitForPlayer(1) === 0)
    assert(gameBank.getMoneyForPlayer(1) === 100)
  }

  test("throw exception when debit not enabled and player does not have enough money") {
    gameBank.makeTransaction(receiverId = 1, 100)
    gameBank.makeTransaction(receiverId = 2, 400)
    assert(gameBank.getMoneyForPlayer(1) === 100)
    assert(gameBank.getMoneyForPlayer(2) === 400)
    assertThrows[IllegalStateException](gameBank.makeTransaction(1, 2, 200))
  }

  test("player with debit cannot make transaction") {
    gameBank.makeTransaction(receiverId = 1, 200)
    gameBank.makeTransaction(receiverId = 2, 1000)
    assert(gameBank.getMoneyForPlayer(1) === 200)
    assert(gameBank.getMoneyForPlayer(2) === 1000)
    gameBank.makeGlobalTransaction(receiverId = 3, 300)
    assert(gameBank.getMoneyForPlayer(2) === 700)
    assert(gameBank.getDebitForPlayer(1) === 100)

    assertThrows[IllegalStateException](gameBank.makeTransaction(1, 2, 100))
  }
