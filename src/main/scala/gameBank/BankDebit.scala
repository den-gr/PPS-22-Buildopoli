package gameBank

trait BankDebit:
  def getDebtsList: Map[Int, Int]
  def getDebitForPlayer(playerId: Int): Int
  def increaseDebit(playerId: Int, amount: Int): Unit
  def decreaseDebit(playerId: Int, amount: Int): Unit

object BankDebit:
  def apply(): BankDebit = BankDebitImpl()
