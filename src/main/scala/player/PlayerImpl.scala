package player

case class PlayerImpl(playerId: Int) extends Player:

  private var playerMoney: Int = 0
  private var pawnPosition: Int = 0
  override def getPlayerMoney: Int = this.playerMoney
  override def setMoney(amount: Int): Unit = this.playerMoney = amount
  override def increaseMoney(amount: Int): Unit = this.playerMoney += amount
  override def decreaseMoney(amount: Int): Unit = this.playerMoney -= amount
  override def getPlayerPawnPosition: Int = this.pawnPosition
  override def setPlayerPawnPosition(position: Int): Unit = this.pawnPosition = position
  override def getPlayerId: Int = this.playerId
