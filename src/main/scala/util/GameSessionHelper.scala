package util

import gameManagement.gameBank.{Bank, GameBankImpl}
import gameManagement.gameOptions.GameOptions
import gameManagement.gameSession.{GameSession, GameSessionImpl}
import gameManagement.gameStore.{GameStore, GameStoreImpl}
import gameManagement.gameTurn.DefaultGameTurn
import lap.Lap
import lap.Lap.MoneyReward
import player.Player

import scala.collection.mutable.ListBuffer
object GameSessionHelper:
  val selector: (ListBuffer[Player], ListBuffer[Int]) => Int =
    (playerList: ListBuffer[Player], playerWithTurn: ListBuffer[Int]) =>
      playerList.filter(el => !playerWithTurn.contains(el.playerId)).head.playerId
  val playerInitialMoney = 200
  val playerInitialCells = 2
  val debtsManagement = true
  val nCells = 10
  val diceFaces = 6

  def DefaultGameSession(): GameSession =
    val gameOptions: GameOptions =
      GameOptions(playerInitialMoney, playerInitialCells, debtsManagement, nCells, diceFaces, selector)
    val gameStore: GameStore = GameStoreImpl()
    val gameTurn: DefaultGameTurn = DefaultGameTurn(gameOptions, gameStore)
    val gameBank: Bank = GameBankImpl(gameOptions, gameStore)
    val gameLap: Lap = Lap(MoneyReward(200, gameBank))

    val gs = GameSessionImpl(gameOptions, gameBank, gameTurn, gameStore, gameLap)
    gs.addManyPlayers(100)
    gs
