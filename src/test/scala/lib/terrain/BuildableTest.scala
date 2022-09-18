package lib.terrain

import org.scalatest.funsuite.AnyFunSuite
import lib.terrain.Purchasable.*
import lib.terrain.Mortgage.DividePriceMortgage
import lib.terrain.RentStrategy.*
import lib.terrain.{Buildable, GroupManager, Purchasable, Terrain, TerrainInfo}
import lib.terrain.Buildable.*
import lib.terrain.Token.*
import lib.terrain.GroupManager.*
import org.scalatest.BeforeAndAfterEach

class BuildableTest extends AnyFunSuite with BeforeAndAfterEach:

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

  var token: Token = _
  var b1: Buildable = _
  var b2: Buildable = _
  var b3: Buildable = _
  var b4: Buildable = _
  var gm: GroupManager = _

  override def beforeEach(): Unit =
    token = Token(Seq(t1, t2), Seq(4, 1), Seq(Seq(250, 500, 1125, 375), Seq(500)), Seq(25, 50))
    b1 = Buildable(p1, token)
    b2 = Buildable(p2, token)
    b3 = Buildable(p3, token)
    b4 = Buildable(p4, token)
    gm = GroupManager(Array(b1, b2, b3, b4))

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

  test("It is possible to know which tokens can be added") {
    assert(b1.listAvailableToken() == Seq(t1))
  }

  test("Tokens can be added") {
    b1.addToken(t1, 3)
    assert(b1.getNumToken(t1) == 3)
  }

  test("If we try to build on a terrain full it is not possible") {
    b1.addToken(t1, 4)
    b1.addToken(t2, 1)
    assert(!b1.canBuild(gm))
  }

  test("If there are tokens the total rent is given by the sum of the basic price with the token bonuses") {
    b1.addToken(t1, 3)
    assert(b1.computeTotalRent(gm) == 1875)
  }

  test("Tokens have a selling price") {
    assert(b1.tokenSellingPrice(t1) == 12)
  }

  test("Tokens can be destroyed") {
    b1.addToken(t1, 3)
    b1.destroyToken(t1, 2)
    assert(b1.getNumToken(t1) == 1)
    assert(b1.computeTotalRent(gm) == 250)
  }
