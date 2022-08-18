package lap

import lap.Lap.*
import org.scalatest.funsuite.AnyFunSuite

class LapTest extends AnyFunSuite:
  val lap: Lap = GameLap(Array(0, 25, 30))
  test("The game lap checks if a player has completed a lap"){
    assert(lap.isNewLap(true, 3, 5))
    assert(!lap.isNewLap(true, 3, 31))
    assert(!lap.isNewLap(false, 3, 5))
    assert(!lap.isNewLap(false, 3, 31))
  }

  test("The game lap can give a reward to the player that has completed a lap"){
    val mock = new Reward {
      override def triggerBonus(): String = "test"
    }
    assert(lap.giveReward(3, mock) == "test")
  }
