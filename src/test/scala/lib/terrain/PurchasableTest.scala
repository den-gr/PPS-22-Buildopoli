package lib.terrain

import org.scalatest.funsuite.AnyFunSuite
import lib.terrain.Mortgage.{DividePriceMortgage, MortgageStrategy}
import lib.terrain.RentStrategy.RentStrategyWithBonus
import lib.terrain.{Purchasable, Terrain, TerrainInfo}

import org.scalatest.BeforeAndAfterEach

class PurchasableTest extends AnyFunSuite with BeforeAndAfterEach:
  var t: Terrain = _
  var p: Purchasable = _

  override def beforeEach(): Unit =
    t = Terrain(TerrainInfo("vicolo corto"), null)
    p = Purchasable(t, 1000, "fucsia", DividePriceMortgage(1000, 3), RentStrategyWithBonus(50, 20))

  test("A property has a buying price") {
    assert(p.price == 1000)
  }

  test("A property has a group") {
    assert(p.group == "fucsia")
  }

  test("A property can be bought") {
    assert(p.state == PurchasableState.IN_BANK)
    assert(p.owner.isEmpty)
    p.changeOwner(Some(10))
    assert(p.owner.get == 10)
    assert(p.state == PurchasableState.OWNED)
    p.changeOwner(Some(2))
    assert(p.owner.get == 2)
    assert(p.state == PurchasableState.OWNED)
  }

  test("A property can be mortgaged by its owner") {
    p.changeOwner(Some(2))
    assert(p.owner.get == 2)
    assert(p.computeMortgage == 1000 / 3)
    p.mortgage()
    assert(p.owner.get == 2)
    assert(p.state == PurchasableState.MORTGAGED)
  }

  test("A property can be removed from mortage") {
    p.changeOwner(Some(2))
    p.mortgage()
    assert(p.state == PurchasableState.MORTGAGED)
    p.removeMortgage()
    assert(p.state == PurchasableState.OWNED)
  }
