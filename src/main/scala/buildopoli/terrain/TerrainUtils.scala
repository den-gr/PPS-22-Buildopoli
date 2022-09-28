package buildopoli.terrain

object TerrainUtils:

  def onlyPurchasable: PartialFunction[Terrain, Purchasable] = new PartialFunction[Terrain, Purchasable]:
    def apply(t: Terrain): Purchasable = t.asInstanceOf[Purchasable]
    def isDefinedAt(t: Terrain): Boolean = t.isInstanceOf[Purchasable]

  def filterPurchasable(terrains: Seq[Terrain], ownerID: Int, state: PurchasableState): Seq[Purchasable] =
    terrains collect onlyPurchasable filter (t => t.state == state && t.owner.get == ownerID)
