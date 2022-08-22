package gameOptions

import player.Player

import scala.collection.mutable.ListBuffer

object Utils:
  def getPlayer(playerId: Int, playersList: ListBuffer[Player]): Player = playersList
    .filter(p => p.playerId.equals(playerId))
    .result()
    .head
