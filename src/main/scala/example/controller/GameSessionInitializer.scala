package example.controller

import lib.gameManagement.gameBank.{Bank, GameBankImpl}
import lib.gameManagement.gameOptions.*
import lib.gameManagement.gameSession.{GameSession, GameSessionImpl}
import lib.gameManagement.gameStore.{GameStore, GameStoreImpl}
import lib.gameManagement.gameTurn.DefaultGameTurn
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
  private val nCells = 10
  private val diceFaces = 2
  private val gameLapMoneyReward = 200

  def createDefaultGameSession(numberOfPlayers: Int): GameSession =
    val gameOptions: GameOptions =
      GameOptions(playerInitialMoney, playerInitialCells, nCells, diceFaces, selector)
    val gameStore: GameStore = GameStoreImpl()
    val gameTurn: DefaultGameTurn = DefaultGameTurn(gameOptions, gameStore)
    val gameBank: Bank = GameBankImpl(gameOptions, gameStore)
    val gameLap: Lap = Lap(MoneyReward(gameLapMoneyReward, gameBank))

    val gs = GameSessionImpl(gameOptions, gameBank, gameTurn, gameStore, gameLap)
    gs.addManyPlayers(numberOfPlayers)
    gs
