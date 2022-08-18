package terrain

import org.scalatest.funsuite.AnyFunSuite
import terrain.Rent.{KnowsTerrains, RentStrategyWithBonus, RentStrategyMultiplier}

class RentTest extends AnyFunSuite:
  val mock: KnowsTerrains = new KnowsTerrains {
    override def getNTerrain: Int = 2
  }
  test("A rent strategy with bonus for each additional terrain"){
    val v = RentStrategyWithBonus(50, 200, mock)
    assert(v.computeTotalRent == 50 + 200)
  }
  test("A rent strategy that multiplies the rent with one less terrain"){
    val mock1: KnowsTerrains = new KnowsTerrains {
      override def getNTerrain: Int = 1
    }
    val v = RentStrategyMultiplier(60, 2, mock1)
    assert(v.computeTotalRent == 60)

    val mock2: KnowsTerrains = new KnowsTerrains {
      private var b: Boolean = true
      override def getNTerrain: Int = b match
        case true => b = false; 4
        case _ => b = true; 3
    }
    val v2 = RentStrategyMultiplier(60, 2, mock2)
    assert(v2.computeTotalRent == 480)
    assert(v2.computeTotalRent == 240)
  }


