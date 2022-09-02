package gameManagement.gameSession

import gameManagement.diceGenerator.Dice
import gameManagement.gameBank.Bank
import gameManagement.gameOptions.GameOptions
import gameManagement.gameStore.GameStore
import gameManagement.gameTurn.GameTurn
import lap.Lap
import player.Player

import scala.collection.mutable.ListBuffer

trait GameSession:
  def gameOptions: GameOptions
  def gameBank: Bank
  def gameTurn: GameTurn
  def gameStore: GameStore
  def gameLap: Lap
  def dice: Dice
  def addOnePlayer(playerId: Option[Int]): Unit
  def addManyPlayers(n: Int): Unit
  def initializePlayer(lastPlayer: Player): Unit
  def setPlayerPosition(playerId: Int, newPosition: Int, isValidLap: Boolean): Unit

object GameSession:
  def apply(
      gameOptions: GameOptions,
      gameBank: Bank,
      gameTurn: GameTurn,
      gameStore: GameStore,
      gameLap: Lap
  ): GameSession =
    GameSessionImpl(gameOptions: GameOptions, gameBank: Bank, gameTurn: GameTurn, gameStore: GameStore, gameLap: Lap)
