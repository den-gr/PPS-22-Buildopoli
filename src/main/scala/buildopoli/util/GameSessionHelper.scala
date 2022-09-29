package buildopoli.util

import buildopoli.gameManagement.gameBank.{Bank, GameBankImpl}
import buildopoli.gameManagement.gameOptions.GameOptions
import buildopoli.gameManagement.gameSession.{GameSession, GameSessionImpl}
import buildopoli.gameManagement.gameStore.GameStore
import buildopoli.gameManagement.gameTurn.{DefaultGameTurn, GameTurn}
import buildopoli.lap.Lap
import buildopoli.lap.Lap.MoneyReward
import buildopoli.player.Player

import scala.collection.mutable.ListBuffer

/** Provide fast and simple creation of [[GameSession]] for the tests */
object GameSessionHelper:
  val selector: (Seq[Player], Seq[Int]) => Int =
    (playerList: Seq[Player], playerWithTurn: Seq[Int]) =>
      playerList.filter(el => !playerWithTurn.contains(el.playerId)).head.playerId
  val playerInitialMoney = 200
  val playerInitialCells = 0
  val diceFaces = 6

  def DefaultGameSession(numPlayers: Int): GameSession =
    val gameOptions: GameOptions =
      GameOptions(playerInitialMoney, playerInitialCells, numPlayers, diceFaces, selector)
    val gameStore: GameStore = GameStore()
    val gameTurn: GameTurn = GameTurn(gameOptions, gameStore)
    val gameBank: Bank = GameBankImpl(gameStore)
    val gameLap: Lap = Lap(MoneyReward(200, gameBank))

    val gs = GameSessionImpl(gameOptions, gameBank, gameTurn, gameStore, gameLap)
    gs
