package lap

import gameBank.{Bank, GameBankImpl}
import lap.Lap.*
import org.scalatest.funsuite.AnyFunSuite
import player.PlayerImpl
import gameOptions.Utils.getPlayer

import scala.collection.mutable.ListBuffer

class LapTest extends AnyFunSuite:
  val lap: Lap = GameLap()
  val nCells: Int = 20
  val currentPosition = 17
  test("The game lap checks if a player has completed a lap and can return the new position"){
    assert(lap.isNewLap(true, currentPosition, 2, nCells)._1 == 19)
    assert(!lap.isNewLap(true, currentPosition, 2, nCells)._2)

    assert(lap.isNewLap(true, currentPosition, 3, nCells)._1 == 20)
    assert(!lap.isNewLap(true, currentPosition, 3, nCells)._2)

    assert(lap.isNewLap(true, currentPosition, 4, nCells)._1 == 1)
    assert(lap.isNewLap(true, currentPosition, 4, nCells)._2)

    assert(lap.isNewLap(true, currentPosition, 6, nCells)._1 == 3)
    assert(lap.isNewLap(true, currentPosition, 6, nCells)._2)
  }

  test("The game lap can give a reward to the player that has completed a lap"){
    val bank = GameBankImpl(ListBuffer(PlayerImpl(52), PlayerImpl(36)), false)
    val reward: Reward = MoneyReward(bank, 500)
    lap.giveReward(36, reward)
    assert(getPlayer(36, bank.playersList).getPlayerMoney == 500)
    lap.giveReward(36, reward)
    assert(getPlayer(36, bank.playersList).getPlayerMoney == 1000)
  }
