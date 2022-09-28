package buildopoli.terrain

import GroupManager.*

/**
 * It represents the strategy to calculate the rent that a player owes to the terrain's owner if
 * there are no token
 */
trait RentStrategy:
  /**
   * Calculates the rent that a player owes to the terrain's owner if
   * there are no token
   * @param ownerID the owner's ID
   * @param group the terrain's group
   * @param groupManager the entity that provides information about the group
   * @return the price of the rent
   */
  def computeTotalRent(ownerID: Int, group: String, groupManager: GroupManager): Int

object RentStrategy :

  /**
   * A rent strategy that adds to the starting price a bonus for every additional terrain in the group
   * @param startingPrice the basic price
   * @param bonus the amount of additional bonus
   */
  case class RentStrategyWithBonus(startingPrice: Int, bonus:Int) extends RentStrategy:
    override def computeTotalRent(ownerID: Int, group: String, gm: GroupManager): Int =
      startingPrice + bonus * (gm.sameGroupTerrainsOwned(ownerID, group) - 1)

  /**
   * A rent strategy that for each additional terrain's multiplies the previous rent
   * @param startingPrice the basic price
   * @param multiplier the multiplier of the previous price
   */
  case class RentStrategyPreviousPriceMultiplier(startingPrice: Int, multiplier: Int) extends RentStrategy:
    def recursion(nTerrains: Int): Int = nTerrains match
      case 1 => startingPrice
      case n => multiplier * recursion(nTerrains-1)
    override def computeTotalRent(ownerID: Int, group: String, gm: GroupManager): Int = recursion(gm.sameGroupTerrainsOwned(ownerID, group))

  /**
   * A rent strategy that multiplies by a factor the starting price if all the terrains in the group belong to the
   * same owner
   * @param startingPrice the basic price
   * @param factor the factor used to multiply the price
   */
  case class BasicRentStrategyFactor(startingPrice: Int, factor: Int) extends RentStrategy:
    override def computeTotalRent(ownerID: Int, group: String, gm: GroupManager): Int = gm.isGroupComplete(ownerID, group) match
      case true => startingPrice * factor
      case false => startingPrice
