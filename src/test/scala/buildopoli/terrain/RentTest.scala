package buildopoli.terrain

import buildopoli.terrain.{Buildable, GroupManager}
import org.scalatest.funsuite.AnyFunSuite
import buildopoli.terrain.GroupManager.*
import buildopoli.terrain.RentStrategy.*

class RentTest extends AnyFunSuite:

  val mock1: GroupManager = new GroupManager {
    override def isGroupComplete(ownerID: Int, group: String): Boolean = true
    override def sameGroupTerrainsOwned(ownerID: Int, group: String): Int = 2
    override def terrainsOwnerCanBuildOn(ownerID: Int): Seq[Buildable] = ???
  }

  val mock2: GroupManager = new GroupManager {
    override def isGroupComplete(ownerID: Int, group: String): Boolean = false
    override def sameGroupTerrainsOwned(ownerID: Int, group: String): Int = 1
    override def terrainsOwnerCanBuildOn(ownerID: Int): Seq[Buildable] = ???
  }

  val mock3: GroupManager = new GroupManager {
    private var b: Boolean = true
    override def isGroupComplete(ownerID: Int, group: String): Boolean = true

    override def sameGroupTerrainsOwned(ownerID: Int, group: String): Int =  b match
      case true => b = false; 4
      case _ => b = true; 3

    override def terrainsOwnerCanBuildOn(ownerID: Int): Seq[Buildable] = ???
  }

  test("A rent strategy with bonus for each additional terrain"){
    val v = RentStrategyWithBonus(50, 200)
    assert(v.computeTotalRent(1, "test", mock1) == 50 + 200)
  }

  test("A rent strategy that multiplies the rent with one less terrain"){
    val v1 = RentStrategyPreviousPriceMultiplier(60, 2)
    assert(v1.computeTotalRent(1, "test", mock2) == 60)

    val v2 = RentStrategyPreviousPriceMultiplier(60, 2)
    assert(v2.computeTotalRent(1, "test", mock3) == 480)
    assert(v2.computeTotalRent(1, "test", mock3) == 240)
  }

  test("A rent strategy that multiplies the basic price for a given factor if the group is complete"){
    val v1 = BasicRentStrategyFactor(50, 2)
    val v2 = BasicRentStrategyFactor(50, 2)
    assert(v1.computeTotalRent(1, "test", mock1) == 100)
    assert(v2.computeTotalRent(1, "test", mock2) == 50)
  }


