package lib.terrain

object TerrainUtils:

  def onlyPurchasable: PartialFunction[Terrain, Purchasable] = new PartialFunction[Terrain, Purchasable]:
    def apply(t: Terrain): Purchasable = t.asInstanceOf[Purchasable]
    def isDefinedAt(t: Terrain): Boolean = t.isInstanceOf[Purchasable]
