package player

trait Player:
  def setPlayerMoney(amount: Int): Unit
  def getPlayerMoney: Int
  def getPlayerPawnPosition: Int
  def setPlayerPawnPosition(position: Int): Unit
  def getPlayerId: Int
