import gameSession.{GameSession, GameSessionImpl}
import org.scalatest.funsuite.AnyFunSuite

class GameSessionTest extends AnyFunSuite:
  val gameSession: GameSession = GameSessionImpl()

  test("playersList has initial size at zero") {
    assert(gameSession.getPlayersList.size === 0)
  }

  test("playerList size increased after adding one element") {
    val previousSize: Int = gameSession.getPlayersList.size
    gameSession.addOnePlayer(Option.empty)
    assert(gameSession.getPlayersList.size === (previousSize + 1))
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
    assert(gameSession.getPlayersList.last.getPlayerId === 15)
    assert(gameSession.getPlayersList.last.getPlayerMoney === 1000)
  }

  test("duplicate player ID existence") {
    val previousSize: Int = gameSession.getPlayersList.size
    gameSession.addOnePlayer(Option.apply(2))
    assert(gameSession.getPlayersList.size === (previousSize + 1))
  }
