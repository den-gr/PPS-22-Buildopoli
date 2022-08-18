package terrain

import Terrain.*
import Mortgage.*
import Rent.*

object Purchasable :

  enum PurchasableState:
    case IN_BANK, OWNED, MORTGAGED

  //The properties of a Purchasable terrain
  trait Purchasable extends Terrain:
    def price: Int
    def group: String
    def state: PurchasableState
    def owner: Option[Int]

    def changeOwner(newOwner: Option[Int]): Purchasable
    def computeTotalRent: Int
    def mortgage: Purchasable
    def computeMortgage: Int

  case class PurchasableTerrain(private val terrain: Terrain, override val price: Int,
                                override val group: String, ms: MortgageStrategy,
                                rs: RentStrategy, override val owner:Option[Int] = Option.empty,
                                override val state:PurchasableState = PurchasableState.IN_BANK) extends Purchasable:
    override val basicInfo: BasicInfo = terrain.basicInfo
    override def triggerBehaviour(): Any = terrain.triggerBehaviour()

    override def changeOwner(newOwner: Option[Int]): Purchasable = (state, newOwner) match
      case (_, None) => PurchasableTerrain(terrain, price, group, ms, rs, newOwner, PurchasableState.IN_BANK)
      case (PurchasableState.IN_BANK, Some(value)) => PurchasableTerrain(terrain, price, group, ms, rs, newOwner, PurchasableState.OWNED)
      case _ => PurchasableTerrain(terrain, price, group, ms, rs, newOwner, state)
    override def computeTotalRent: Int = rs.computeTotalRent
    override def mortgage: Purchasable = state match
      case PurchasableState.OWNED => PurchasableTerrain(terrain, price, group, ms, rs, owner, PurchasableState.MORTGAGED)
    override def computeMortgage: Int = ms.computeMortgage
