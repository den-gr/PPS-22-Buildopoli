package gameSession

import gameBank.Bank
import gameOptions.{GameOptions, GameTemplate}
import player.Player

import scala.collection.mutable.ListBuffer

trait GameSession:
  def gameOptions: GameOptions
  def gameTemplate: GameTemplate
  def addOnePlayer(playerId: Option[Int]): Unit
  def addManyPlayers(n: Int): Unit
  def getPlayersList: ListBuffer[Player]
  def gameBank: Bank

object GameSession:
  def apply(gameOptions: GameOptions, gameTemplate: GameTemplate): GameSession =
    GameSessionImpl(gameOptions, gameTemplate)
