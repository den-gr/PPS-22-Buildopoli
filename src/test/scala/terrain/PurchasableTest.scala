package terrain

import org.scalatest.funsuite.AnyFunSuite
import terrain.Mortgage.{DividePriceMortgage, MortgageStrategy}
import terrain.RentStrategy.RentStrategyWithBonus
import terrain.Terrain

class PurchasableTest extends AnyFunSuite:

    val bt: Terrain = Terrain(TerrainInfo("vicolo corto", 1, null))
    var p: Purchasable = Purchasable(bt, 1000, "fucsia", DividePriceMortgage(1000, 3), RentStrategyWithBonus(50, 20))

    test("A property has a buying price"){
      assert(p.price == 1000)
    }
    test("A property has a group"){
      assert(p.group == "fucsia")
    }
    test("A property can be bought"){
      assert(p.state == PurchasableState.IN_BANK)
      assert(p.owner.isEmpty)
      p = p.changeOwner(Some(10))
      assert(p.owner.get == 10)
      assert(p.state == PurchasableState.OWNED)
      p = p.changeOwner(Some(2))
      assert(p.owner.get == 2)
      assert(p.state == PurchasableState.OWNED)
    }
    test("A property can be mortgaged by its owner") {
      p = p.changeOwner(Some(2))
      assert(p.owner.get == 2)
      assert(p.computeMortgage == 1000/3)
      p = p.mortgage
      assert(p.owner.get == 2)
      assert(p.state == PurchasableState.MORTGAGED)
    }