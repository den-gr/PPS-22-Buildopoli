package lib.behaviour

/** Allows correctly navigate between events of a behaviour
  * @tparam T
  *   data structure that contains events of behaviour
  */
trait GenericBehaviourExplorer[T]:
  /** Index of an event of the behaviour
    */
  type Index

  /** Id of player that explore behaviour events
    */
  val playerId: Int

  /** @return
    *   false if there are not available events to explore
    */
  def hasNext: Boolean

  /** Triggers the selected event and update [[GenericBehaviourExplorer.currentEvents]]
    * @param index
    *   define what event will be selected
    */
  def next(index: Index): Unit

  /** @return
    *   events available to the player
    */
  def currentEvents: T

  /** @return
    *   true if there are not mandatory events, so a player can end his turn
    */
  def canEndExploring: Boolean

  /** Empty explorer
    */
  def endExploring(): Unit
