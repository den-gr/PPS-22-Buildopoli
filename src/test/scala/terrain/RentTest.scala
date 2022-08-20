package terrain

import org.scalatest.funsuite.AnyFunSuite
import terrain.Rent.{BasicRentStrategyFactor, KnowsTerrains, RentStrategyPreviousPriceMultiplier, RentStrategyWithBonus}

class RentTest extends AnyFunSuite:
  val mock1: KnowsTerrains = new KnowsTerrains {
    override def getNTerrain: Int = 2
    override def isGroupComplete: Boolean = true
  }

  val mock2: KnowsTerrains = new KnowsTerrains {
    override def getNTerrain: Int = 1
    override def isGroupComplete: Boolean = false
  }

  val mock3: KnowsTerrains = new KnowsTerrains {
    private var b: Boolean = true
    override def getNTerrain: Int = b match
      case true => b = false; 4
      case _ => b = true; 3
    override def isGroupComplete: Boolean = true
  }

  test("A rent strategy with bonus for each additional terrain"){
    val v = RentStrategyWithBonus(50, 200, mock1)
    assert(v.computeTotalRent == 50 + 200)
  }

  test("A rent strategy that multiplies the rent with one less terrain"){
    val v1 = RentStrategyPreviousPriceMultiplier(60, 2, mock2)
    assert(v1.computeTotalRent == 60)

    val v2 = RentStrategyPreviousPriceMultiplier(60, 2, mock3)
    assert(v2.computeTotalRent == 480)
    assert(v2.computeTotalRent == 240)
  }

  test("A rent strategy that multiplies the basic price for a given factor if the group is complete"){
    val v1 = BasicRentStrategyFactor(50, 2, mock1)
    val v2 = BasicRentStrategyFactor(50, 2, mock2)
    assert(v1.computeTotalRent == 100)
    assert(v2.computeTotalRent == 50)
  }


