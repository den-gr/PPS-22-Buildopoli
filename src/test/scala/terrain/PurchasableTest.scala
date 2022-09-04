package terrain

import org.scalatest.funsuite.AnyFunSuite
import terrain.Mortgage.{DividePriceMortgage, MortgageStrategy}
import terrain.RentStrategy.RentStrategyWithBonus

class PurchasableTest extends AnyFunSuite:

  val t: Terrain = Terrain(TerrainInfo("vicolo corto", 1), null)
  val p: Purchasable = Purchasable(t, 1000, "fucsia", DividePriceMortgage(1000, 3), RentStrategyWithBonus(50, 20))

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
