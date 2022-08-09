import gameSession.{GameSession, GameSessionImpl}
import org.scalatest.funsuite.AnyFunSuite
import todo.{GameOptions, GameTemplate}

class GameBankTest extends AnyFunSuite:
  val gameTemplate: GameTemplate = new GameTemplate
  val gameOption: GameOptions = new GameOptions
  val gameSession: GameSession = GameSessionImpl(gameOption, gameTemplate)
  val playerId = 14

  test("player has incremented money") {
    gameSession.addOnePlayer(Option.apply(playerId))
    assert(gameSession.getPlayersList.size === 1)
    gameSession.getGameBank.setPlayerMoney(playerId, 1000)
    assert(gameSession.getPlayersList.filter(p => p.getPlayerId.equals(playerId)).result().head.getPlayerMoney === 1000)
  }

  test("player has decremented money") {
    gameSession.getGameBank.setPlayerMoney(playerId, -100)
    assert(gameSession.getPlayersList.filter(p => p.getPlayerId.equals(playerId)).result().head.getPlayerMoney === 900)
  }

  test("two players make a transaction") {
    val newPlayer = playerId + 1
    gameSession.addOnePlayer(Option.apply(newPlayer))
    assert(gameSession.getPlayersList.size === 2)
    gameSession.getGameBank.setPlayerMoney(newPlayer, 500)
    assert(
      gameSession.getPlayersList.filter(p => p.getPlayerId.equals(newPlayer)).result().head.getPlayerMoney === 500
    )
    assert(gameSession.getPlayersList.filter(p => p.getPlayerId.equals(playerId)).result().head.getPlayerMoney === 900)
    gameSession.getGameBank.makeTransaction(playerId + 1, playerId, 300)
    assert(
      gameSession.getPlayersList.filter(p => p.getPlayerId.equals(newPlayer)).result().head.getPlayerMoney === 200
    )
    assert(gameSession.getPlayersList.filter(p => p.getPlayerId.equals(playerId)).result().head.getPlayerMoney === 1200)
  }
