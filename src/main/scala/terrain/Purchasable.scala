package terrain

import Terrain.*
import Mortgage.*
import Rent.*
import terrain.GroupManager.GroupManager

object Purchasable :

  /**
   * This enum represent the state of a purchasable terrain
   * IN_BANK if the terrain has not been purchased by anyone
   * OWNED if a owner possess the terrain and it is active
   * MORTGAGED if a owner possess the terrain but they have temporarily sold it to the bank so it is not active
   */
  enum PurchasableState:
    case IN_BANK, OWNED, MORTGAGED

  /**
   * It represents the properties of a terrain that can be purchased by a player
   */
  trait Purchasable extends Terrain:
    /**
     * @return the price at which the player can buy the terrain
     */
    def price: Int
    /**
     *
     * @return the group to which the terrain belongs
     */
    def group: String
    /**
     *
     * @return the state the terrain is currently in
     */
    def state: PurchasableState
    /**
     *
     * @return the owner of the terrain
     */
    def owner: Option[Int]
    /**
     * It is used to change the ownership of the terrain
     * @param newOwner the new owner of the terrain
     * @return a new Purchasable object
     */
    def changeOwner(newOwner: Option[Int]): Purchasable
    /**
     *
     * @param gm is the group manager that provides information about the terrain's group
     * @return the total rent a player owes to the owner of the terrain
     */
    def computeTotalRent(gm: GroupManager): Int
    /**
     * It is used to mortgage the purchasable terrain to the bank
     * @return a new Purchasable object
     */
    def mortgage: Purchasable
    /**
     *
     * @return the total amount of money the owner gets if mortgage the purchasable terrain
     */
    def computeMortgage: Int

  /**
   * The standard implementation of a purchasable terrain
   * @param terrain the basic terrain
   * @param price the price at which the terrain can be bought
   * @param group the group to which the terrain belongs
   * @param ms the strategy to compute the mortgage price
   * @param rs the strategy to compute the rent
   * @param owner the owner of the terrain
   * @param state the state of the terrain in game
   */
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
    override def computeTotalRent(gm: GroupManager): Int = rs.computeTotalRent(owner.get, group,gm)
    override def mortgage: Purchasable = state match
      case PurchasableState.OWNED => PurchasableTerrain(terrain, price, group, ms, rs, owner, PurchasableState.MORTGAGED)
    override def computeMortgage: Int = ms.computeMortgage
