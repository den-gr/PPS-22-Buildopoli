package gameBank

trait BankDebit:
  def getDebitForPlayer(playerId: Int): Int
  def getDebitList(): Map[Int, Int]
  def increaseDebit(playerId: Int, amount: Int): Unit
  def decreaseDebit(playerId: Int, amount: Int): Unit
  
