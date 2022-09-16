package lib.gameManagement.gameTurn

trait GameJail:

  /** List of players blocked in doing some turns. Because of they are in Jail for example.
    */
  var blockingList: Map[Int, Int] = Map()

  /** To block a player (ex. in prison)
    *
    * @param playerId
    *   identifying one player
    * @param blockingTime
    *   number of turns to stay blocked
    */
  def lockPlayer(playerId: Int, blockingTime: Int): Unit

  /** To liberate player (from prison)
    *
    * @param playerId
    *   identifying one player
    */
  def liberatePlayer(playerId: Int): Unit

  /** To reduce the time of blocked players
    */
  def doTurn(): Unit

  /** @param playerId
    *   identifying one player
    * @return
    *   remaining blocked movements for the given player. If blocked.
    */
  def getRemainingBlockedMovements(playerId: Int): Option[Int]
