package lib.gameManagement.gameSession

import lib.gameManagement.gameBank.{Bank, GameBankImpl}
import org.scalatest.funsuite.AnyFunSuite
import lib.lap.Lap.MoneyReward
import lib.gameManagement.gameOptions.GameOptions
import lib.gameManagement.gameSession.{GameSession, GameSessionImpl}
import lib.gameManagement.gameStore.{GameStore, GameStoreImpl}
import lib.gameManagement.gameTurn.{DefaultGameTurn, GameTurn}
import lib.lap.Lap
import lib.player.Player
import lib.terrain.Mortgage.DividePriceMortgage
import lib.terrain.RentStrategy.RentStrategyWithBonus
import lib.terrain.{Purchasable, Terrain, TerrainInfo}

import scala.collection.immutable.Seq
import scala.collection.mutable.ListBuffer

class GameSessionTest extends AnyFunSuite:

  val selector: (Seq[Player], Seq[Int]) => Int =
    (playerList: Seq[Player], playerWithTurn: Seq[Int]) =>
      playerList.filter(el => !playerWithTurn.contains(el.playerId)).head.playerId

  val gameStore: GameStore = GameStoreImpl()
  val gameOptions: GameOptions = GameOptions(200, 2, 10, 6, selector)
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

  test("game started control") {
    gameSession.startGame()
    assertThrows[InterruptedException](gameSession.addOnePlayer(Option.apply(2)))
  }
