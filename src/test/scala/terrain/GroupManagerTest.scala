package terrain

import org.scalatest.funsuite.AnyFunSuite
import terrain.Buildable.BuildableTerrain
import terrain.GroupManager.*
import terrain.Purchasable.*
import terrain.Terrain.*
import terrain.Buildable.*

class GroupManagerTest extends AnyFunSuite:
  val t0: Terrain = BasicTerrain(TerrainInfo("safe zone", 0, null))
  val t1: Purchasable = PurchasableTerrain(BasicTerrain(TerrainInfo("purple 1" , 1, null)), 500, "purple", null, null, Some(2), PurchasableState.OWNED)
  val t2: Purchasable = PurchasableTerrain(BasicTerrain(TerrainInfo("purple 2" , 1, null)), 500, "purple", null, null)
  val t3: Buildable = BuildableTerrain(PurchasableTerrain(BasicTerrain(TerrainInfo("red 1" , 1, null)), 500, "red", null, null, Some(1), PurchasableState.OWNED), null)
  val t4: Buildable = BuildableTerrain(PurchasableTerrain(BasicTerrain(TerrainInfo("red 2" , 1, null)), 500, "red", null, null, Some(1), PurchasableState.OWNED), null)
  val t5: Buildable = BuildableTerrain(PurchasableTerrain(BasicTerrain(TerrainInfo("red 3" , 1, null)), 500, "red", null, null, Some(1), PurchasableState.OWNED), null)

  val terrains: Seq[Terrain] = Array(t0, t1, t2, t3, t4, t5)
  val gp: GroupManager = GameGroupManager(terrains)
  test("If the owner has no terrain of the group, the group is not complete"){
    assert(!gp.isGroupComplete(0, "purple"))
  }

  test("if the group does not exist the group is not complete"){
    assert(!gp.isGroupComplete(1, "prova"))
  }

  test("if the owner does not possess all the terrains of a group the group is not complete"){
    assert(!gp.isGroupComplete(2, "purple"))
  }

  test("If the owner possess all the terrains of a group the group is complete"){
    assert(gp.isGroupComplete(1, "red"))
  }

  test("It is possible to know how many terrains the owner owns"){
    assert(gp.sameGroupTerrainsOwned(1, "red") == 3)
    assert(gp.sameGroupTerrainsOwned(2, "purple") == 1)
    assert(gp.sameGroupTerrainsOwned(2, "red") == 0)
  }

