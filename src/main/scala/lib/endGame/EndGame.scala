package lib.endGame

import lib.gameManagement.gameStore.*
import lib.terrain.{Purchasable, PurchasableState, Terrain}
import lib.player.Player

trait EndGame:

  def deleteDefeatedPlayer(strategy: Player => Boolean): Unit

object EndGame:

  def defeatedForNoMoneyAndNoTerrainsOwned(player: Player, gameStore: GameStore): Boolean =
    player.getPlayerMoney == 0 && terrainPerPlayer(gameStore.terrainList, player.playerId) == 0

  case class NoMoneyNoTerrains(gameStore: GameStore) extends EndGame:

    override def deleteDefeatedPlayer(strategy: Player => Boolean): Unit =
      deleteTerrains(gameStore.playersList filter (p => strategy(p)) map (p => p.playerId))
      gameStore.playersList = gameStore.playersList filter (p => !strategy(p))

    private def deleteTerrains(ownerIds: Seq[Int]): Unit =
      for t <- gameStore.terrainList collect onlyPurchasable filter (x =>
          x.state != PurchasableState.IN_BANK && (ownerIds contains x.owner.get)
        )
      do t.changeOwner(None)

  private def terrainPerPlayer(terrains: Seq[Terrain], id: Int): Int =
    terrains collect onlyPurchasable count (t => t.state == PurchasableState.OWNED && t.owner.get == id)

  private def onlyPurchasable = new PartialFunction[Terrain, Purchasable]:
    def apply(t: Terrain): Purchasable = t.asInstanceOf[Purchasable]
    def isDefinedAt(t: Terrain): Boolean = t.isInstanceOf[Purchasable]
