package gameBank

case class BankDebitImpl() extends BankDebit:

  var debtsList: Map[Int, Int] = Map()
  override def getDebtsList: Map[Int, Int] = this.debtsList

  override def getDebitForPlayer(playerId: Int): Int =
    this.debtsList.getOrElse(playerId, 0)

  override def increaseDebit(playerId: Int, amount: Int): Unit =
    updateDebtsList(playerId, amount)

  override def decreaseDebit(playerId: Int, amount: Int): Unit =
    updateDebtsList(playerId, -amount)

  def updateDebtsList(playerId: Int, amount: Int): Unit =
    this.debtsList += (playerId, amount + getDebitForPlayer(playerId))
