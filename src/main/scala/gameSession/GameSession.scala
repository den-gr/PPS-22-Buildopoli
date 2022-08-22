package gameSession

import gameBank.Bank
import gameOptions.GameOptions
import lap.Lap.{Lap, Reward}
import player.Player

import scala.collection.mutable.ListBuffer

trait GameSession:
  def gameOptions: GameOptions
  def gameLap: Lap
  def gameBank: Bank
  def addOnePlayer(playerId: Option[Int]): Unit
  def addManyPlayers(n: Int): Unit
  def getPlayersList: ListBuffer[Player]
  def initializePlayer(): Unit
  def setPlayerPosition(playerId: Int, newPosition: Int, isValidLap: Boolean, lapReward: Reward): Unit

object GameSession:
  def apply(gameOptions: GameOptions, gameLap: Lap): GameSession =
    GameSessionImpl(gameOptions, gameLap)
