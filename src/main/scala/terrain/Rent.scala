package terrain

import Buildable.Token
object Rent :

  trait KnowsTerrains:
    def getNTerrain: Int
    def isGroupComplete: Boolean

  trait RentStrategy:
    def computeTotalRent: Int

  case class RentStrategyWithBonus(startingPrice: Int, bonus:Int, kt: KnowsTerrains) extends RentStrategy:
    override def computeTotalRent: Int = startingPrice + bonus * (kt.getNTerrain - 1)

  case class RentStrategyPreviousPriceMultiplier(startingPrice: Int, multiplier: Int, kt: KnowsTerrains) extends RentStrategy:
    def recursion(nTerrains: Int): Int = nTerrains match
      case 1 => startingPrice
      case n => multiplier * recursion(nTerrains-1)
    override def computeTotalRent: Int = recursion(kt.getNTerrain)

  case class BasicRentStrategyFactor(startingPrice: Int, factor: Int, kt: KnowsTerrains) extends RentStrategy:
    override def computeTotalRent: Int = kt.isGroupComplete match
      case true => startingPrice * factor
      case false => startingPrice
