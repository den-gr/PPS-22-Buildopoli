package buildopoli.player

case class PlayerImpl(override val playerId: Int) extends Player:

  var playerMoney: Int = 0
  var pawnPosition: Int = 0
  override def getPlayerMoney: Int = this.playerMoney
  override def setPlayerMoney(amount: Int): Unit = this.playerMoney = amount
  override def getPlayerPawnPosition: Int = this.pawnPosition
  override def setPlayerPawnPosition(position: Int): Unit = this.pawnPosition = position
