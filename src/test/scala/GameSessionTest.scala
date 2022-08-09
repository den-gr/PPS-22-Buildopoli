import gameSession.{GameSession, GameSessionImpl}
import org.scalatest.funsuite.AnyFunSuite
import todo.{GameOptions, GameTemplate}

class GameSessionTest extends AnyFunSuite:
  val gameTemplate: GameTemplate = new GameTemplate
  val gameOption: GameOptions = new GameOptions
  val gameSession: GameSession = GameSessionImpl(gameOption, gameTemplate)

  test("playersList has initial size at zero") {
    assert(gameSession.getPlayersList.size === 0)
  }

  test("playerList size increased after adding one element") {
    val previousSize: Int = gameSession.getPlayersList.size
    val previousSizeBank: Int = gameSession.getGameBank.getPlayersList.size
    gameSession.addOnePlayer(Option.empty)
    assert(gameSession.getGameBank.getPlayersList.size === (previousSizeBank + 1))
    assert(gameSession.getPlayersList.size === (previousSize + 1))
  }

  test("playerList size increased after adding multiple elements") {
    val previousSize: Int = gameSession.getPlayersList.size
    gameSession.addManyPlayers(5)
    assert(gameSession.getPlayersList.size === (previousSize + 5))
  }
