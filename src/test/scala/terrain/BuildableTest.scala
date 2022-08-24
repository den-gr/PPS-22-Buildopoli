package terrain

import org.scalatest.funsuite.AnyFunSuite
import Purchasable.*
import terrain.Mortgage.DividePriceMortgage
import terrain.Rent.{BasicRentStrategyFactor, RentStrategyWithBonus}
import terrain.Terrain.{BasicTerrain, Terrain, TerrainInfo}
import terrain.Buildable.*
import Token.*
import terrain.GroupManager.*

class BuildableTest extends AnyFunSuite:

  val t: Terrain = BasicTerrain(TerrainInfo("vicolo corto", 1, null))
  val p1: Purchasable = PurchasableTerrain(t, 1000, "fucsia", DividePriceMortgage(1000, 3), BasicRentStrategyFactor(50, 3), Some(2), PurchasableState.OWNED)
  val p2: Purchasable = PurchasableTerrain(t, 1000, "fucsia", DividePriceMortgage(1000, 3), BasicRentStrategyFactor(50, 3), Some(2), PurchasableState.OWNED)

  val t1: String = "house"
  val t2: String = "hotel"
  val token: Token = TokenWithBonus(Map(t1 -> Array(250, 500, 1125, 375), t2 -> Array(500)), Array(4, 1), Array(25, 50), Map(t1 -> 0, t2 -> 0))
  var b1: Buildable = BuildableTerrain(p1, token)
  val b2: Buildable = BuildableTerrain(p2, token)

  val gm: GroupManager = GameGroupManager(Array(b1, b2))

  test("A token where the array size is not coherent is not valid"){
    assertThrows[Exception](TokenWithBonus(Map(t1 -> Array(250, 500, 1125, 375), t2 -> Array(500)), Array(4, 1), Array(25, 50), Map(t2 -> 0)))
    assertThrows[Exception](TokenWithBonus(Map(t1 -> Array(250, 500, 1125, 375), t2 -> Array(500)), Array(4), Array(25, 50), Map(t1 -> 0, t2 -> 0)))
  }

  test("A token where the number of bonus is not the same as the max is not valid") {
    assertThrows[Exception](TokenWithBonus(Map(t1 -> Array(250, 500, 1125, 375), t2 -> Array(500)), Array(5, 1), Array(25, 50), Map(t1 -> 0, t2 -> 0)))
  }

  test("A token where the name types do not match is not valid"){
    assertThrows[Exception](TokenWithBonus(Map(t1 -> Array(250, 500, 1125, 375), t2 -> Array(500)), Array(4, 1), Array(25, 50), Map(t1 -> 0, "wrong" -> 0)))
  }

  test("If there are no token and the group is complete the price is the basic one multiplied by the factor"){
    assert(b1.getNumToken(t1) == 0)
    assert(b1.computeTotalRent(gm) == 150)
  }

  test("If there are no token and the group is not complete the price is the basic one"){
    assert(b2.getNumToken(t1) == 0)
    assert(b2.computeTotalRent(gm) == 50)
  }

  test("Tokens have a price"){
    assert(b1.tokenBuyingPrice(t1) == 25)
    assert(b1.tokenBuyingPrice(t2) == 50)
  }

  test("Tokens can be added"){
    b1 = b1.addToken(t1, 3)
    assert(b1.getNumToken(t1) == 3)
  }

  test("If there are token the total rent is given by the sum of the basic price with the bonuses given by the tokens"){
    assert(b1.computeTotalRent(gm) == 1875)
  }

  test("Tokens have a selling price"){
    assert(b1.tokenSellingPrice(t1) == 12)
  }

  test("Tokens can be destroyed"){
    b1 = b1.destroyToken(t1, 2)
    assert(b1.getNumToken(t1) == 1)
    assert(b1.computeTotalRent(gm) == 250)
  }

