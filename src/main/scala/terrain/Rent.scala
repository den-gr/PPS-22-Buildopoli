package terrain

object Rent :

  trait KnowsTerrains:
    def getNTerrain: Int

  trait RentStrategy:
    def computeTotalRent: Int

  case class RentStrategyWithBonus(startingPrice: Int, bonus:Int, kt: KnowsTerrains) extends RentStrategy:
    override def computeTotalRent: Int = startingPrice + bonus * (kt.getNTerrain - 1)

  case class RentStrategyMultiplier(startingPrice:Int, multiplier: Int, kt: KnowsTerrains) extends RentStrategy:
    def recursion(nTerrains: Int): Int = nTerrains match
      case 1 => startingPrice
      case n => multiplier * recursion(nTerrains-1)
    override def computeTotalRent: Int = recursion(kt.getNTerrain)
