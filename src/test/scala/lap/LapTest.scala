package lap

import gameManagement.gameBank.{Bank, GameBankImpl}
import gameManagement.gameOptions.GameOptions
import gameManagement.gameStore.{GameStore, GameStoreImpl}
import lap.Lap.*
import org.scalatest.funsuite.AnyFunSuite
import player.{Player, PlayerImpl}

import scala.collection.mutable.ListBuffer

class LapTest extends AnyFunSuite:

  val selector: (ListBuffer[Player], ListBuffer[Int]) => Int =
    (playerList: ListBuffer[Player], playerWithTurn: ListBuffer[Int]) =>
      playerList.filter(el => !playerWithTurn.contains(el.playerId)).head.playerId

  val gameStore: GameStore = GameStoreImpl()
  val gameOptions: GameOptions = GameOptions(200, 2, true, 20, selector)
  val gameBank: Bank = GameBankImpl(gameOptions, gameStore)
  val lap: Lap = GameLap(MoneyReward(500, gameBank))

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

    assert(lap.isNewLap(false, currentPosition, 6, nCells)._1 == 3)
    assert(!lap.isNewLap(false, currentPosition, 6, nCells)._2)

    assert(lap.isNewLap(true, currentPosition, -6, nCells)._1 == 11)
    assert(!lap.isNewLap(true, currentPosition, -6, nCells)._2)

    assert(lap.isNewLap(true, 2, -3, nCells)._1 == 19)
    assert(!lap.isNewLap(true, 2, -3, nCells)._2)
  }

  test("The game lap can give a reward to the player that has completed a lap"){
    gameStore.playersList += PlayerImpl(1)
    gameStore.playersList += PlayerImpl(2)

    lap.giveReward(1)
    assert(gameStore.getPlayer(1).getPlayerMoney == 500)
    lap.giveReward(1)
    assert(gameStore.getPlayer(1).getPlayerMoney == 1000)
  }
