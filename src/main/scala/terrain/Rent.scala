package terrain

object Rent :
  /**
   * It represents the entity that knows the information related to the terrain's group
   */
  trait KnowsTerrains:
    def getNTerrain: Int
    def isGroupComplete: Boolean

  /**
   * It represents the strategy to calculate the rent that a player owes to the terrain's owner if
   * there are no token
   */
  trait RentStrategy:
    def computeTotalRent: Int

  /**
   * A rent strategy that adds to the starting price a bonus for every additional terrain in the group
   * @param startingPrice the basic price
   * @param bonus the amount of additional bonus
   * @param kt the entity that provide the information of the terrain's group
   */
  case class RentStrategyWithBonus(startingPrice: Int, bonus:Int, kt: KnowsTerrains) extends RentStrategy:
    override def computeTotalRent: Int = startingPrice + bonus * (kt.getNTerrain - 1)

  /**
   * A rent strategy that for each additional terrain's multiplies the previous rent
   * @param startingPrice the basic price
   * @param multiplier the multiplier of the previous price
   * @param kt the entity that provide the information of the terrain's group
   */
  case class RentStrategyPreviousPriceMultiplier(startingPrice: Int, multiplier: Int, kt: KnowsTerrains) extends RentStrategy:
    def recursion(nTerrains: Int): Int = nTerrains match
      case 1 => startingPrice
      case n => multiplier * recursion(nTerrains-1)
    override def computeTotalRent: Int = recursion(kt.getNTerrain)

  /**
   * A rent strategy that multiplies by a factor the starting price if all the terrains in the group belong to the
   * same owner
   * @param startingPrice the basic price
   * @param factor the factor used to multiply the price
   * @param kt the entity that provide the information of the terrain's group
   */
  case class BasicRentStrategyFactor(startingPrice: Int, factor: Int, kt: KnowsTerrains) extends RentStrategy:
    override def computeTotalRent: Int = kt.isGroupComplete match
      case true => startingPrice * factor
      case false => startingPrice
