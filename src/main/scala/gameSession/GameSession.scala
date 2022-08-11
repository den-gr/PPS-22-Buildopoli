package gameSession

import gameBank.Bank
import player.Player

import scala.collection.mutable.ListBuffer

trait GameSession:
  def addOnePlayer(playerId: Option[Int]): Unit
  def addManyPlayers(n: Int): Unit
  def getPlayersList: ListBuffer[Player]
  def getGameBank: Bank
