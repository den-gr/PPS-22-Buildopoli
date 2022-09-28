package buildopoli.terrain

/** It represents the entity that provides the information about a terrain's group
  */
trait GroupManager:
  /** It tells if a owner has completed a certain group
    * @param ownerID
    *   the owner to consider
    * @param group
    *   the group to consider
    * @return
    *   if the group is complete or not
    */
  def isGroupComplete(ownerID: Int, group: String): Boolean

  /** It tells how many terrains of a certain group a owner possess
    * @param ownerID
    *   the owner to consider
    * @param group
    *   the group to consider
    * @return
    *   the number of terrains
    */
  def sameGroupTerrainsOwned(ownerID: Int, group: String): Int

  def terrainsOwnerCanBuildOn(ownerID: Int): Seq[Buildable]

object GroupManager:
  import buildopoli.terrain.TerrainUtils.onlyPurchasable

  /** The group manager implementation that works with the complete sequence of terrains
    * @param terrains
    *   the complete sequence of terrains
    */
  def apply(terrains: Seq[Terrain]): GroupManager = GameGroupManager(terrains)

  private case class GameGroupManager(terrains: Seq[Terrain]) extends GroupManager:
    override def isGroupComplete(ownerID: Int, group: String): Boolean =
      val terrainInGroup: Int = terrains collect onlyPurchasable count (t => t.group == group)
      terrainInGroup != 0 && terrainInGroup == sameGroupTerrainsOwned(ownerID, group)

    override def sameGroupTerrainsOwned(ownerID: Int, group: String): Int =
      terrains collect onlyPurchasable count (t =>
        t.state == PurchasableState.OWNED && t.owner.contains(ownerID) && t.group == group
      )

    override def terrainsOwnerCanBuildOn(ownerID: Int): Seq[Buildable] = terrains filter (t =>
      t match
        case t: Buildable => isGroupComplete(ownerID, t.group)
        case _ => false
    ) map (t => t.asInstanceOf[Buildable])
