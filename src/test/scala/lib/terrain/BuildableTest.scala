package lib.terrain

import org.scalatest.funsuite.AnyFunSuite
import lib.terrain.Purchasable.*
import lib.terrain.Mortgage.DividePriceMortgage
import lib.terrain.RentStrategy.*
import lib.terrain.{Buildable, GroupManager, Purchasable, Terrain, TerrainInfo, Token}
import lib.terrain.Buildable.*
import lib.terrain.Token.*
import lib.terrain.GroupManager.*

class BuildableTest extends AnyFunSuite:

  val t: Terrain = Terrain(TerrainInfo("vicolo corto"), null)
  val p1: Purchasable = Purchasable(
    t,
    1000,
    "fucsia",
    DividePriceMortgage(1000, 3),
    BasicRentStrategyFactor(50, 3),
    Some(2),
    PurchasableState.OWNED
  )
  val p2: Purchasable = Purchasable(
    t,
    1000,
    "fucsia",
    DividePriceMortgage(1000, 3),
    BasicRentStrategyFactor(50, 3),
    Some(2),
    PurchasableState.OWNED
  )
  val p3: Purchasable = Purchasable(
    t,
    1000,
    "red",
    DividePriceMortgage(1000, 3),
    BasicRentStrategyFactor(50, 3),
    Some(2),
    PurchasableState.OWNED
  )
  val p4: Purchasable = Purchasable(t, 1000, "red", DividePriceMortgage(1000, 3), BasicRentStrategyFactor(50, 3))

  val t1: String = "house"
  val t2: String = "hotel"
  val token: Token =
    Token(Map(t1 -> Array(250, 500, 1125, 375), t2 -> Array(500)), Array(4, 1), Array(25, 50), Map(t1 -> 0, t2 -> 0))
  var b1: Buildable = Buildable(p1, token)
  val b2: Buildable = Buildable(p2, token)
  val b3: Buildable = Buildable(p3, token)
  val b4: Buildable = Buildable(p4, token)

  val gm: GroupManager = GroupManager(Array(b1, b2, b3, b4))

  test("A token where the array size is not coherent is not valid") {
    assertThrows[Exception](
      Token(Map(t1 -> Array(250, 500, 1125, 375), t2 -> Array(500)), Array(4, 1), Array(25, 50), Map(t2 -> 0))
    )
    assertThrows[Exception](
      Token(Map(t1 -> Array(250, 500, 1125, 375), t2 -> Array(500)), Array(4), Array(25, 50), Map(t1 -> 0, t2 -> 0))
    )
  }

  test("A token where the number of bonus is not the same as the max is not valid") {
    assertThrows[Exception](
      Token(Map(t1 -> Array(250, 500, 1125, 375), t2 -> Array(500)), Array(5, 1), Array(25, 50), Map(t1 -> 0, t2 -> 0))
    )
  }

  test("A token where the name types do not match is not valid") {
    assertThrows[Exception](
      Token(
        Map(t1 -> Array(250, 500, 1125, 375), t2 -> Array(500)),
        Array(4, 1),
        Array(25, 50),
        Map(t1 -> 0, "wrong" -> 0)
      )
    )
  }

  test("If there are no token and the group is complete the price is the basic one multiplied by the factor") {
    assert(b1.getNumToken(t1) == 0)
    assert(b1.computeTotalRent(gm) == 150)
  }

  test("If there are no token and the group is not complete the price is the basic one") {
    assert(b3.getNumToken(t1) == 0)
    assert(b3.computeTotalRent(gm) == 50)
  }

  test("Tokens have a price") {
    assert(b1.tokenBuyingPrice(t1) == 25)
    assert(b1.tokenBuyingPrice(t2) == 50)
  }

  test("It is possible to check if it is possible to build on a terrain") {
    assert(b1.canBuild(gm))
    assert(!b3.canBuild(gm))
  }

  test("Tokens can be added") {
    b1.addToken(t1, 3)
    assert(b1.getNumToken(t1) == 3)
  }

  test(
    "If there are token the total rent is given by the sum of the basic price with the bonuses given by the tokens"
  ) {
    assert(b1.computeTotalRent(gm) == 1875)
  }

  test("Tokens have a selling price") {
    assert(b1.tokenSellingPrice(t1) == 12)
  }

  test("Tokens can be destroyed") {
    b1.destroyToken(t1, 2)
    assert(b1.getNumToken(t1) == 1)
    assert(b1.computeTotalRent(gm) == 250)
  }
