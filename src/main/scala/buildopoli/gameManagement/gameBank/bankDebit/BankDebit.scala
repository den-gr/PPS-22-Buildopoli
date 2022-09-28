package buildopoli.gameManagement.gameBank.bankDebit

/** Represents the debit manage into the game. Debit is limiting the potential of one player. If it has debit it can
  * only make transactions to sell something, not to buy. Other players can request him some money, that will increase
  * his debit.
  */
trait BankDebit:

  /** @return
    *   a map containing lists of players with each value of debit
    */
  def getDebtsList: Map[Int, Int]

  /** @param playerId
    *   the id of the player
    * @return
    *   debit value associated with the given player id
    */
  def getDebitOfPlayer(playerId: Int): Int

  /** To increase debit value of a specific player
    * @param playerId
    *   the id of the player with increased debit
    * @param amount
    *   of debit to add
    */
  def increaseDebit(playerId: Int, amount: Int): Unit

  /** To decrease debit value for a specific player id
    * @param playerId
    *   the id of the player
    * @param amount
    *   of debit to reduce from the previous value
    */
  def decreaseDebit(playerId: Int, amount: Int): Unit

object BankDebit:
  def apply(): BankDebit = BankDebitImpl()
