import gameSession.{GameSession, GameSessionImpl}
import org.scalatest.funsuite.AnyFunSuite

class GameBankTest extends AnyFunSuite:

  val gameSession: GameSession = GameSessionImpl()
  var playerCounter = 0

  test("player has incremented money") {
    addPlayer(1)
    gameSession.getGameBank.increasePlayerMoney(1, 1000)
    assert(gameSession.getGameBank.getMoneyForPlayer(1) === 1000)
  }

  test("player has decremented money") {
    gameSession.getGameBank.decreasePlayerMoney(1, 100)
    assert(gameSession.getGameBank.getMoneyForPlayer(1) === 900)
  }

  test("two players make a transaction") {
    addPlayer(2)
    gameSession.getGameBank.increasePlayerMoney(2, 500)
    gameSession.getGameBank.makeTransaction(2, 1, 300)
    assert(gameSession.getGameBank.getMoneyForPlayer(2) === 200)
    assert(gameSession.getGameBank.getMoneyForPlayer(1) === 1200)
  }

  test("player has debit (and zero money) after decreasing money") {
    addPlayer(3)
    gameSession.getGameBank.increasePlayerMoney(3, 500)
    gameSession.getGameBank.decreasePlayerMoney(3, 600)
    assert(gameSession.getGameBank.getDebtsForPlayer(3) === 100)
    assert(gameSession.getGameBank.getMoneyForPlayer(3) === 0)
  }

  test("player has decreased debit after money increase") {
    assert(gameSession.getGameBank.getDebtsForPlayer(3) === 100)
    gameSession.getGameBank.increasePlayerMoney(3, 50)
    assert(gameSession.getGameBank.getDebtsForPlayer(3) === 50)
  }

  test("player has increased debit after money decrease") {
    assert(gameSession.getGameBank.getDebtsForPlayer(3) === 50)
    gameSession.getGameBank.decreasePlayerMoney(3, 150)
    assert(gameSession.getGameBank.getDebtsForPlayer(3) === 200)
  }

  test("player has zero debit") {
    assert(gameSession.getGameBank.getDebtsForPlayer(3) === 200)
    gameSession.getGameBank.increasePlayerMoney(3, 250)
    assert(gameSession.getGameBank.getDebtsForPlayer(3) === 0)
    assert(gameSession.getGameBank.getMoneyForPlayer(3) === 50)
  }

  test("initial player has debit 0") {
    addPlayer(4)
    assert(gameSession.getGameBank.getDebtsForPlayer(4) === 0)
  }

  def addPlayer(id: Int): Unit =
    gameSession.addOnePlayer(Option.apply(id))
    playerCounter += 1
    assert(gameSession.getPlayersList.size === playerCounter)
