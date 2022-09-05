package terrain

import org.scalatest.funsuite.AnyFunSuite
import terrain.Buildable.BuildableTerrain
import terrain.GroupManager.*
import terrain.Purchasable.*
import terrain.Terrain.*
import terrain.Buildable.*

class GroupManagerTest extends AnyFunSuite:
  val t0: Terrain = Terrain(TerrainInfo("safe zone", 0), null)
  val t1: Purchasable = Purchasable(Terrain(TerrainInfo("purple 1" , 1), null), 500, "purple", null, null, Some(2), PurchasableState.OWNED)
  val t2: Purchasable = Purchasable(Terrain(TerrainInfo("purple 2" , 1), null), 500, "purple", null, null)
  val t3: Buildable = Buildable(Purchasable(Terrain(TerrainInfo("red 1" , 1), null), 500, "red", null, null, Some(1), PurchasableState.OWNED), null)
  val t4: Buildable = Buildable(Purchasable(Terrain(TerrainInfo("red 2" , 1), null), 500, "red", null, null, Some(1), PurchasableState.OWNED), null)
  val t5: Buildable = Buildable(Purchasable(Terrain(TerrainInfo("red 3" , 1), null), 500, "red", null, null, Some(1), PurchasableState.OWNED), null)
  val t6: Purchasable = Purchasable(Terrain(TerrainInfo("blue 1" , 1), null), 500, "blue", null, null, Some(2), PurchasableState.MORTGAGED)
  val t7: Purchasable = Purchasable(Terrain(TerrainInfo("blue 2" , 1), null), 500, "blue", null, null, Some(2), PurchasableState.OWNED)

  val terrains: Seq[Terrain] = Array(t0, t1, t2, t3, t4, t5, t6, t7)
  val gp: GroupManager = GroupManager(terrains)
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

  test("If a terrain is mortgaged it is not count"){
    assert(gp.sameGroupTerrainsOwned(2, "blue") == 1)
    assert(!gp.isGroupComplete(2, "blue"))

  }

