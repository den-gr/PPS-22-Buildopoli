package buildopoli.lap

import buildopoli.gameManagement.gameBank.{Bank, GameBankImpl}
import buildopoli.lap.Lap.*
import buildopoli.gameManagement.gameOptions.GameOptions
import buildopoli.gameManagement.gameStore.GameStore
import buildopoli.lap.Lap
import buildopoli.player.{Player, PlayerImpl}
import org.scalatest.funsuite.AnyFunSuite

import scala.collection.mutable.ListBuffer

class LapTest extends AnyFunSuite:

  val selector: (Seq[Player], Seq[Int]) => Int =
    (playerList: Seq[Player], playerWithTurn: Seq[Int]) =>
      playerList.filter(el => !playerWithTurn.contains(el.playerId)).head.playerId

  val gameStore: GameStore = GameStore()
  val gameOptions: GameOptions = GameOptions(200, 2, 20, 6, selector)
  val gameBank: Bank = GameBankImpl(gameStore)
  val lap: Lap = Lap(MoneyReward(500, gameBank))

  val nCells: Int = 20
  val currentPosition = 17
  test("The game lap checks if a player has completed a lap and can return the new position") {
    assert(lap.isNewLap(true, currentPosition, 2, nCells)._1 == 19)
    assert(!lap.isNewLap(true, currentPosition, 2, nCells)._2)

    assert(lap.isNewLap(true, currentPosition, 3, nCells)._1 == 0)
    assert(lap.isNewLap(true, currentPosition, 3, nCells)._2)

    assert(lap.isNewLap(true, currentPosition, 4, nCells)._1 == 1)
    assert(lap.isNewLap(true, currentPosition, 4, nCells)._2)

    assert(lap.isNewLap(true, currentPosition, 6, nCells)._1 == 3)
    assert(lap.isNewLap(true, currentPosition, 6, nCells)._2)

    assert(lap.isNewLap(false, currentPosition, 6, nCells)._1 == 3)
    assert(!lap.isNewLap(false, currentPosition, 6, nCells)._2)

    assert(lap.isNewLap(true, currentPosition, -6, nCells)._1 == 11)
    assert(!lap.isNewLap(true, currentPosition, -6, nCells)._2)

    assert(lap.isNewLap(true, 2, -3, nCells)._1 == 19)
    assert(!lap.isNewLap(true, 2, -3, nCells)._2)
  }

  test("The game lap can give a reward to the player that has completed a lap") {
    gameStore.addPlayer()
    gameStore.addPlayer()

    lap.giveReward(1)
    assert(gameStore.getPlayer(1).getPlayerMoney == 500)
    lap.giveReward(1)
    assert(gameStore.getPlayer(1).getPlayerMoney == 1000)
  }
