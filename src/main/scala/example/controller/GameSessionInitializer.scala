package example.controller

import buildopoli.endGame.EndGame
import buildopoli.gameManagement.gameBank.{Bank, GameBankImpl}
import buildopoli.gameManagement.gameOptions.*
import buildopoli.gameManagement.gameSession.{GameSession, GameSessionImpl}
import buildopoli.gameManagement.gameStore.GameStore
import buildopoli.gameManagement.gameTurn.{DefaultGameTurn, GameTurn}
import buildopoli.lap.Lap
import buildopoli.lap.Lap.MoneyReward
import buildopoli.player.*

/**
 * Build default [[GameSession]] 
 */
trait GameSessionInitializer:
  /**
   * @param numberOfPlayer number of player that will participate in the game
   * @return built game session
   */
  def createDefaultGameSession(numberOfPlayer: Int): GameSession

object GameSessionInitializer extends GameSessionInitializer:
  private val selector: (Seq[Player], Seq[Int]) => Int =
    (playerList: Seq[Player], playerWithTurn: Seq[Int]) =>
      playerList.find(el => !playerWithTurn.contains(el.playerId)).head.playerId
  private val playerInitialMoney = 200
  private val playerInitialCells = 0
  private val diceFaces = 3
  private val gameLapMoneyReward = 100

  def createDefaultGameSession(numberOfPlayers: Int): GameSession =
    val gameStore: GameStore = GameStore()
    val gameBank: Bank = GameBankImpl(gameStore)
    val endGame = playerId => EndGame.defeatedForNoMoneyAndNoTerrainsOwned(playerId, gameStore, gameBank)
    val gameOptions: GameOptions =
      GameOptions(playerInitialMoney, playerInitialCells, numberOfPlayers, diceFaces, selector, endGame)
    val gameTurn: GameTurn = GameTurn(gameOptions, gameStore)
    val gameLap: Lap = Lap(MoneyReward(gameLapMoneyReward, gameBank))

    val gs = GameSessionImpl(gameOptions, gameBank, gameTurn, gameStore, gameLap)
    gs
