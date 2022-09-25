package lib.player

/** Represents the player into the game.
  */
trait Player:

  /** @return
    *   player's unique id
    */
  def playerId: Int

  /** Sets player's money to a certain amount
    * @param amount
    *   of money to set the player
    */
  def setPlayerMoney(amount: Int): Unit

  /** @return
    *   player's money
    */
  def getPlayerMoney: Int

  /** @return
    *   player's pawn position
    */
  def getPlayerPawnPosition: Int

  /** Set player's position
    * @param position
    *   for a specific terrain into the game
    */
  def setPlayerPawnPosition(position: Int): Unit

object Player:
  def apply(playerId: Int): Player = PlayerImpl(playerId)
