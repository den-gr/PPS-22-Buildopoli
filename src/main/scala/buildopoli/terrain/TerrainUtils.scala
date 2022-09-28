package buildopoli.terrain

object TerrainUtils:

  /** A partial function that allows to filter all the the terrains that can be purchased and to map them into
    * purchasable terrain
    * @return
    *   the implemented function
    */
  def onlyPurchasable: PartialFunction[Terrain, Purchasable] = new PartialFunction[Terrain, Purchasable]:
    def apply(t: Terrain): Purchasable = t.asInstanceOf[Purchasable]
    def isDefinedAt(t: Terrain): Boolean = t.isInstanceOf[Purchasable]

  /** It allows to filter all the terrains that can be purchased that are in a specific state and that belong to a
    * chosen owner
    * @param terrains
    *   that we need to check
    * @param ownerID
    *   id of the owner we need to check
    * @param state
    *   that we need to check
    * @return
    *   the terrains that meet the condition
    */
  def filterPurchasable(terrains: Seq[Terrain], ownerID: Int, state: PurchasableState): Seq[Purchasable] =
    terrains collect onlyPurchasable filter (t => t.state == state && t.owner.get == ownerID)
