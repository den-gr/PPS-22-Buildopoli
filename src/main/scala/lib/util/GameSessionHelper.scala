package lib.util

import lib.gameManagement.gameBank.{Bank, GameBankImpl}
import lib.gameManagement.gameOptions.GameOptions
import lib.gameManagement.gameSession.{GameSession, GameSessionImpl}
import lib.gameManagement.gameStore.GameStore
import lib.gameManagement.gameTurn.{DefaultGameTurn, GameTurn}
import lib.lap.Lap
import lib.lap.Lap.MoneyReward
import lib.player.Player

import scala.collection.mutable.ListBuffer
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
