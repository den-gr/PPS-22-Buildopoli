package lib.terrain.card

import lib.behaviour.BehaviourModule.Behaviour
import lib.behaviour.event.EventModule.{Event, EventStrategy}
import lib.behaviour.event.EventStoryModule.EventStory
import lib.lap.Lap.MoneyReward
import lib.behaviour.event.EventGroup
import lib.gameManagement.gameBank.{Bank, GameBankImpl}
import lib.gameManagement.gameOptions.GameOptions
import lib.gameManagement.gameSession.{GameSession, GameSessionImpl}
import lib.gameManagement.gameStore.{GameStore, GameStoreImpl}
import lib.gameManagement.gameTurn.{DefaultGameTurn, GameTurn}
import lib.lap.Lap
import lib.player.Player
import lib.terrain.card.{CardTerrain, DefaultCards}
import lib.terrain.{Terrain, TerrainInfo}
import org.scalatest.funsuite.AnyFunSuite

class CardTerrainTest extends AnyFunSuite:

  val selector: (Seq[Player], Seq[Int]) => Int =
    (playerList: Seq[Player], playerWithTurn: Seq[Int]) =>
      playerList.filter(el => !playerWithTurn.contains(el.playerId)).head.playerId

  val gameStore: GameStore = GameStoreImpl()
  val gameOptions: GameOptions = GameOptions(200, 0, 5, 6, selector)
  val gameBank: Bank = GameBankImpl(gameOptions, gameStore)
  val gameTurn: GameTurn = DefaultGameTurn(gameOptions, gameStore)
  val gameLap: Lap = Lap(MoneyReward(200, gameBank))

  val gameSession: GameSession = GameSessionImpl(gameOptions, gameBank, gameTurn, gameStore, gameLap)

  val t: Terrain = Terrain(TerrainInfo("carta probabilità", 1), Behaviour())
  val t1: Terrain = Terrain(TerrainInfo("carta imprevisti", 2), Behaviour())

  val probabilityTerrain: CardTerrain = CardTerrain.apply(t)
  val surpriseTerrain: CardTerrain = CardTerrain.apply(t1, gameSession, true)

  test("adding 4 players to gameSession") {
    gameSession.gameStore.putTerrain(probabilityTerrain, surpriseTerrain)
    gameSession.startGame()

    assert(gameSession.gameStore.terrainList.size == 2)
    assert(gameSession.gameStore.playersList.size == 5)
  }

  test("Player being in the probability terrain, take a cart, so his money should be incremented from 200 to 700") {
    val addMoneyStory: EventStory = EventStory("Test", "Add 500 money")
    val addMoneyStrategy: EventStrategy = id => gameSession.gameBank.makeTransaction(receiverId = id, 500)
    val addMoney = DefaultCards(EventGroup(Event(addMoneyStory, addMoneyStrategy)), "add money")
    probabilityTerrain.addCards(addMoney)

    gameSession.setPlayerPosition(1, 1)
    assert(gameSession.getPlayerPosition(1) == 1)
    assert(gameSession.getTerrain(gameSession.getPlayerPosition(1)) == probabilityTerrain)

    val behaviour = gameSession.getTerrain(gameSession.getPlayerPosition(1)).getBehaviourIterator(1)
    behaviour.next()

    assert(gameSession.gameBank.getMoneyForPlayer(1) == 700)

    probabilityTerrain.removeCard(addMoney.name)
    assert(probabilityTerrain.cardList.isEmpty)
  }

  test("removing money from player 1, giving one card of probability") {
    val removeMoneyStory: EventStory = EventStory("Test", "Remove 500 money")
    val removeMoneyStrategy: EventStrategy = id => gameSession.gameBank.makeTransaction(id, amount = 500)
    val removeMoney = DefaultCards(EventGroup(Event(removeMoneyStory, removeMoneyStrategy)), "remove money")
    probabilityTerrain.addCards(removeMoney)

    assert(gameSession.getPlayerPosition(1) == 1)
    assert(gameSession.getTerrain(gameSession.getPlayerPosition(1)) == probabilityTerrain)

    val behaviour = gameSession.getTerrain(gameSession.getPlayerPosition(1)).getBehaviourIterator(1)
    behaviour.next()

    assert(gameSession.gameBank.getMoneyForPlayer(1) == 200)

    probabilityTerrain.removeCard(removeMoney.name)
    assert(probabilityTerrain.cardList.isEmpty)
  }

  test("player have to do one entire lap to get an extra bonus") {
    val doOneLapStory: EventStory = EventStory("Test", "Do One Lap and stop at the start cell")
    val doOneLapStrategy: EventStrategy =
      id =>
        gameSession.setPlayerPosition(
          id,
          (gameSession.gameStore.getNumberOfTerrains(_ => true) - gameSession.getPlayerPosition(id)) + 1
        )
    val doOneLap = DefaultCards(EventGroup(Event(doOneLapStory, doOneLapStrategy)), "do one lap")
    probabilityTerrain.addCards(doOneLap)

    assert(gameSession.getPlayerPosition(1) == 1)
    assert(gameSession.getTerrain(gameSession.getPlayerPosition(1)) == probabilityTerrain)

    val behaviour = gameSession.getTerrain(gameSession.getPlayerPosition(1)).getBehaviourIterator(1)
    behaviour.next()

    assert(gameSession.getPlayerPosition(1) == 1)
    assert(gameSession.gameBank.getMoneyForPlayer(1) == 400)

    probabilityTerrain.removeCard(doOneLap.name)
    assert(probabilityTerrain.cardList.isEmpty)
  }

  test("testing some surprises cards") {
    gameSession.setPlayerPosition(1, 1)
    assert(gameSession.getPlayerPosition(1) == 2)
    assert(gameSession.getTerrain(gameSession.getPlayerPosition(1)) == surpriseTerrain)

    val behaviour = gameSession.getTerrain(gameSession.getPlayerPosition(1)).getBehaviourIterator(1)
    println(behaviour.currentStories)
    behaviour.next()
  }
