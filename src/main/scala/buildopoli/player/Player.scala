package buildopoli.player

trait Player:
  def playerId: Int
  def setPlayerMoney(amount: Int): Unit
  def getPlayerMoney: Int
  def getPlayerPawnPosition: Int
  def setPlayerPawnPosition(position: Int): Unit

object Player:
  def apply(playerId: Int): Player = PlayerImpl(playerId)
