package buildopoli.terrain

import Terrain.*
import Mortgage.*
import RentStrategy.*

/** This enum represent the state of a purchasable terrain IN_BANK if the terrain has not been purchased by anyone OWNED
  * if a owner possess the terrain and it is active MORTGAGED if a owner possess the terrain but they have temporarily
  * sold it to the bank so it is not active
  */
enum PurchasableState:
  case IN_BANK, OWNED, MORTGAGED

/** It represents the properties of a terrain that can be purchased by a player
  */
trait Purchasable extends Terrain:
  /** @return
    *   the price at which the player can buy the terrain
    */
  def price: Int

  /** @return
    *   the group to which the terrain belongs
    */
  def group: String

  /** @return
    *   the state the terrain is currently in
    */
  def state: PurchasableState

  /** @return
    *   the owner of the terrain
    */
  def owner: Option[Int]

  /** It is used to change the ownership of the terrain
    * @param newOwner
    *   the new owner of the terrain
    */
  def changeOwner(newOwner: Option[Int]): Unit

  /** @param gm
    *   is the group manager that provides information about the terrain's group
    * @return
    *   the total rent a player owes to the owner of the terrain
    */
  def computeTotalRent(gm: GroupManager): Int

  /** It is used to mortgage the purchasable terrain to the bank
    */
  def mortgage(): Unit

  /** It is used to remove the purchasable terrain from the mortgage
    */
  def removeMortgage(): Unit

  /** @return
    *   the total amount of money the owner gets if mortgage the purchasable terrain
    */
  def computeMortgage: Int

object Purchasable:

  /** A factory to create a purchasable terrain that, by default, has no owner
    * @param terrain
    *   the basic terrain
    * @param price
    *   the price at which the terrain can be bought
    * @param group
    *   the group to which the terrain belongs
    * @param ms
    *   the strategy to compute the mortgage price
    * @param rs
    *   the strategy to compute the rent
    */
  def apply(terrain: Terrain, price: Int, group: String, ms: MortgageStrategy, rs: RentStrategy): Purchasable =
    PurchasableTerrain(terrain, price, group, ms, rs, Option.empty, PurchasableState.IN_BANK)

  /** A factory to create a purchasable terrain with or without owner based on the value of the ownerID
    * @param terrain
    *   the basic terrain
    * @param price
    *   the price at which the terrain can be bought
    * @param group
    *   the group to which the terrain belongs
    * @param ms
    *   the strategy to compute the mortgage price
    * @param rs
    *   the strategy to compute the rent
    * @param owner
    *   the owner of the terrain
    */
  def apply(
      terrain: Terrain,
      price: Int,
      group: String,
      ms: MortgageStrategy,
      rs: RentStrategy,
      owner: Option[Int]
  ): Purchasable =
    owner match
      case Some(_) => PurchasableTerrain(terrain, price, group, ms, rs, owner, PurchasableState.OWNED)
      case _ => PurchasableTerrain(terrain, price, group, ms, rs, owner, PurchasableState.IN_BANK)

  /** A factory to create a purchasable terrain with the desired combination of owner and state
    * @param terrain
    *   the basic terrain
    * @param price
    *   the price at which the terrain can be bought
    * @param group
    *   the group to which the terrain belongs
    * @param ms
    *   the strategy to compute the mortgage price
    * @param rs
    *   the strategy to compute the rent
    * @param owner
    *   the owner of the terrain
    * @param state
    *   the state of the terrain in game
    */
  def apply(
      terrain: Terrain,
      price: Int,
      group: String,
      ms: MortgageStrategy,
      rs: RentStrategy,
      owner: Option[Int],
      state: PurchasableState
  ): Purchasable =
    owner match
      case None => PurchasableTerrain(terrain, price, group, ms, rs, owner, PurchasableState.IN_BANK)
      case _ => PurchasableTerrain(terrain, price, group, ms, rs, owner, state)

  private case class PurchasableTerrain(
      private val terrain: Terrain,
      override val price: Int,
      override val group: String,
      ms: MortgageStrategy,
      rs: RentStrategy,
      private var ownerID: Option[Int],
      private var purchasableState: PurchasableState
  ) extends Purchasable:
    export terrain.*

    override def owner: Option[Int] = ownerID
    override def state: PurchasableState = purchasableState
    override def changeOwner(newOwner: Option[Int]): Unit = (state, newOwner) match
      case (_, None) => ownerID = newOwner; purchasableState = PurchasableState.IN_BANK
      case (PurchasableState.IN_BANK, Some(value)) => ownerID = newOwner; purchasableState = PurchasableState.OWNED
      case _ => ownerID = newOwner; purchasableState = state
    override def computeTotalRent(gm: GroupManager): Int = rs.computeTotalRent(owner.get, group, gm)
    override def mortgage(): Unit = state match
      case PurchasableState.OWNED => purchasableState = PurchasableState.MORTGAGED
    override def removeMortgage(): Unit = state match
      case PurchasableState.MORTGAGED => purchasableState = PurchasableState.OWNED
    override def computeMortgage: Int = ms.computeMortgage
