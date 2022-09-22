package example.controller

import lib.gameManagement.gameBank.{Bank, GameBankImpl}
import lib.gameManagement.gameOptions.*
import lib.gameManagement.gameSession.{GameSession, GameSessionImpl}
import lib.gameManagement.gameStore.GameStore
import lib.gameManagement.gameTurn.{DefaultGameTurn, GameTurn}
import lib.lap.Lap
import lib.lap.Lap.MoneyReward
import lib.player.*

trait GameSessionInitializer:
  def createDefaultGameSession(numberOfPlayer: Int): GameSession

object GameSessionInitializer extends GameSessionInitializer:
  private val selector: (Seq[Player], Seq[Int]) => Int =
    (playerList: Seq[Player], playerWithTurn: Seq[Int]) =>
      playerList.filter(el => !playerWithTurn.contains(el.playerId)).head.playerId
  private val playerInitialMoney = 200
  private val playerInitialCells = 0
  private val diceFaces = 2
  private val gameLapMoneyReward = 200

  def createDefaultGameSession(numberOfPlayers: Int): GameSession =
    val gameOptions: GameOptions =
      GameOptions(playerInitialMoney, playerInitialCells, numberOfPlayers, diceFaces, selector)
    val gameStore: GameStore = GameStore()
    val gameTurn: GameTurn = GameTurn(gameOptions, gameStore)
    val gameBank: Bank = GameBankImpl(gameStore)
    val gameLap: Lap = Lap(MoneyReward(gameLapMoneyReward, gameBank))

    val gs = GameSessionImpl(gameOptions, gameBank, gameTurn, gameStore, gameLap)
    gs
