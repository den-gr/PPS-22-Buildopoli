package terrain

import org.scalatest.funsuite.AnyFunSuite
import Terrain.*

class TerrainTest extends AnyFunSuite:

  val t1: Terrain = Terrain(TerrainInfo("tassa di lusso", 1, null))

  test("A terrain has a name") {
    assert(t1.basicInfo.name == "tassa di lusso")
  }
  test("A terrain has a position") {
    assert(t1.basicInfo.position == 1)
  }
  test("A terrain can trigger a behaviour") {
    assert(t1.triggerBehaviour() == "test behaviour")
  }
