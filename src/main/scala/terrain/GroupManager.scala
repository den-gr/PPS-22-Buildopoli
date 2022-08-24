package terrain

import terrain.Purchasable.Purchasable
import terrain.Terrain.Terrain

object GroupManager :

  trait GroupManager:
    def isGroupComplete(ownerID: Int, group: String, terrains: Seq[Terrain]): Boolean
    def sameGroupTerrainsOwned(ownerID: Int, group: String, terrains: Seq[Terrain]): Int

  case class GameGroupManager() extends GroupManager:
    override def isGroupComplete(ownerID: Int, group: String, terrains: Seq[Terrain]): Boolean =
      val terrainInGroup: Int = terrains collect onlyPurchasable count (t => t.group == group)
      terrainInGroup != 0 && terrainInGroup == sameGroupTerrainsOwned(ownerID, group, terrains)

    override def sameGroupTerrainsOwned(ownerID: Int, group: String, terrains: Seq[Terrain]): Int = (terrains collect onlyPurchasable count (t => t.owner.contains(ownerID) && t.group == group))

    private def onlyPurchasable = new PartialFunction[Terrain, Purchasable] {
      def apply(t: Terrain): Purchasable = t.asInstanceOf[Purchasable]
      def isDefinedAt(t: Terrain): Boolean = t.isInstanceOf[Purchasable]
    }
