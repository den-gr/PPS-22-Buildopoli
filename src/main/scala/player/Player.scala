package player

trait Player:
  def setMoney(amount: Int): Unit
  def increaseMoney(amount: Int): Unit
  def decreaseMoney(amount: Int): Unit
  def getPlayerMoney: Int
  def getPlayerPawnPosition: Int
  def setPlayerPawnPosition(position: Int): Unit
  def getPlayerId: Int
