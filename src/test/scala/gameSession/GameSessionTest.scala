package gameSession

import gameManagement.gameBank.{Bank, GameBankImpl}
import gameManagement.gameOptions.GameOptions
import gameManagement.gameSession.{GameSession, GameSessionImpl}
import gameManagement.gameStore.{GameStore, GameStoreImpl}
import gameManagement.gameTurn.{DefaultGameTurn, GameTurn}
import org.scalatest.funsuite.AnyFunSuite
import player.Player
import lap.Lap
import lap.Lap.MoneyReward

import scala.collection.mutable.ListBuffer

class GameSessionTest extends AnyFunSuite:

  val selector: (ListBuffer[Player], ListBuffer[Int]) => Int =
    (playerList: ListBuffer[Player], playerWithTurn: ListBuffer[Int]) =>
      playerList.filter(el => !playerWithTurn.contains(el.playerId)).head.playerId

  val gameStore: GameStore = GameStoreImpl()
  val gameOptions: GameOptions = GameOptions(200, 2, true, 10, selector)
  val gameBank: Bank = GameBankImpl(gameOptions, gameStore)
  val gameTurn: GameTurn = DefaultGameTurn(gameOptions, gameStore)
  val gameLap: Lap = Lap(MoneyReward(200, gameBank))

  val gameSession: GameSession = GameSessionImpl(gameOptions, gameBank, gameTurn, gameStore, gameLap)

  test("playersList has initial size at zero") {
    assert(gameStore.playersList.size === 0)
  }

  test("playerList size increased after adding one element") {
    val previousSize: Int = gameStore.playersList.size
    gameSession.addOnePlayer(Option.empty)
    assert(gameStore.playersList.size === (previousSize + 1))
    assert(gameSession.gameBank.gameStore.playersList.size === (previousSize + 1))
    assert(gameSession.gameStore.playersList.size === (previousSize + 1))
  }

  test("playerList size increased after adding multiple elements") {
    val previousSize: Int = gameStore.playersList.size
    gameSession.addManyPlayers(5)
    assert(gameStore.playersList.size === (previousSize + 5))
  }

  test("last inserted player has money of 1000 after being created") {
    val previousSize: Int = gameStore.playersList.size
    gameSession.addOnePlayer(Option.apply(15))
    assert(gameStore.playersList.size === (previousSize + 1))
    assert(gameStore.playersList.last.playerId === 15)
    assert(gameSession.gameStore.playersList.last.getPlayerMoney === 200)
  }

  test("duplicate player ID existence") {
    val previousSize: Int = gameStore.playersList.size
    gameSession.addOnePlayer(Option.apply(2))
    assert(gameStore.playersList.size === (previousSize + 1))
  }


