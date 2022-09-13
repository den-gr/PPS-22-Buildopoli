package lib.player

import lib.player.{Player, PlayerImpl}
import org.scalatest.funsuite.AnyFunSuite

class PlayerTest extends AnyFunSuite:
  var playerIdsCounter: Int = 0
  val player: Player = PlayerImpl(playerIdsCounter)

  test("player money equals to zero") {
    assert(player.getPlayerMoney === 0)
  }

  test("player pawn position equals to zero") {
    assert(player.getPlayerPawnPosition === 0)
  }
