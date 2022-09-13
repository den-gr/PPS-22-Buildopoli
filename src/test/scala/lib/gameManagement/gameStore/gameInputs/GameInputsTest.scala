package lib.gameManagement.gameStore.gameInputs

import lib.gameManagement.gameOptions.GameOptions
import lib.gameManagement.gameStore.gameInputs.{GameInputs, UserInputs}
import lib.gameManagement.gameStore.{GameStore, GameStoreImpl}
import lib.gameManagement.gameTurn.{DefaultGameTurn, GameTurn}
import lib.player.{Player, PlayerImpl}
import org.scalatest.funsuite.AnyFunSuite

class GameInputsTest extends AnyFunSuite:
  val userInput: GameInputs = UserInputs()

  val selector: (Seq[Player], Seq[Int]) => Int =
    (playerList: Seq[Player], playerWithTurn: Seq[Int]) =>
      playerList.filter(el => !playerWithTurn.contains(el.playerId)).head.playerId

  val gameStore: GameStore = GameStoreImpl()
  val gameOptions: GameOptions = GameOptions(200, 2, 10, 6, selector)
  val gameTurn: GameTurn = DefaultGameTurn(gameOptions, gameStore)

  test("adding one tail element") {
    userInput.addTailInputEvent("tailElement")
    assert(!userInput.isListEmpty)
    assert(userInput.getHeadElement === "tailElement")
  }

  test("removing the head element, adding one tail element") {
    userInput.addTailInputEvent("secondTailElement")
    assert(!userInput.isListEmpty)
    assert(userInput.getHeadElement === "tailElement")
    userInput.removeHeadElement()
    assert(userInput.getHeadElement === "secondTailElement")
  }

  test("assert gameInput inside gameStore is empty") {
    assert(gameStore.userInputs.isListEmpty)
  }

  test("adding two players and testing turns with list not empty") {
    gameStore.addPlayer(PlayerImpl(1))
    gameStore.addPlayer(PlayerImpl(2))
    assert(gameTurn.selectNextPlayer() === 1)
    assert(gameTurn.playerWithTurn.head === 1)
    gameStore.userInputs.addTailInputEvent("inputElement")
    assertThrows[RuntimeException](gameTurn.selectNextPlayer() === 2)
  }

  test("emptying list should allow to proceed with turns") {
    gameStore.userInputs.removeHeadElement()
    assert(gameStore.userInputs.isListEmpty)
    assert(gameTurn.selectNextPlayer() === 2)
    assert(gameTurn.playerWithTurn.isEmpty)
  }
