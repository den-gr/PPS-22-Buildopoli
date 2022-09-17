package lib.terrain.card

import lib.behaviour.BehaviourModule.Behaviour
import lib.behaviour.event.EventModule.{Event, EventStrategy}
import lib.behaviour.event.story.EventStoryModule.EventStory
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
import org.scalatest.BeforeAndAfterEach
import org.scalatest.funsuite.AnyFunSuite

class CardTerrainTest extends AnyFunSuite with BeforeAndAfterEach:

  var gameSession: GameSession = _
  var probabilityTerrain: CardTerrain = _
  var surpriseTerrain: CardTerrain = _
  override def beforeEach(): Unit =
    val selector: (Seq[Player], Seq[Int]) => Int =
      (playerList: Seq[Player], playerWithTurn: Seq[Int]) =>
        playerList.filter(el => !playerWithTurn.contains(el.playerId)).head.playerId
    val gameStore: GameStore = GameStoreImpl()
    val gameOptions: GameOptions = GameOptions(200, 0, 5, 6, selector)
    val gameBank: Bank = GameBankImpl(gameStore)
    val gameTurn: GameTurn = DefaultGameTurn(gameOptions, gameStore)
    val gameLap: Lap = Lap(MoneyReward(200, gameBank))

    gameSession = GameSessionImpl(gameOptions, gameBank, gameTurn, gameStore, gameLap)

    val t: Terrain = Terrain(TerrainInfo("carta probabilità"), Behaviour())
    val t1: Terrain = Terrain(TerrainInfo("carta imprevisti"), Behaviour())
    probabilityTerrain = CardTerrain.apply(t)
    surpriseTerrain = CardTerrain.apply(t1, gameSession, true)
    gameSession.gameStore.putTerrain(Terrain(TerrainInfo("partenza"), Behaviour()), probabilityTerrain, surpriseTerrain)

  test("adding 4 players to gameSession") {
    gameSession.startGame()

    assert(gameSession.gameStore.terrainList.size == 3)
    assert(gameSession.gameStore.playersList.size == 5)
  }

  test(
    "Player being in the probability terrain, take a cart, " +
      "so his money should be incremented from 200 to 700"
  ) {
    gameSession.startGame()

    val addMoneyStory: EventStory = EventStory("Test", "Add 500 money")
    val addMoneyStrategy: EventStrategy = id => gameSession.gameBank.makeTransaction(receiverId = id, 500)
    val addMoney = DefaultCards(EventGroup(Event(addMoneyStory, addMoneyStrategy)), "add money")
    probabilityTerrain.addCards(addMoney)

    gameSession.movePlayer(1, steps = 1)
    assert(gameSession.getPlayerPosition(1) == 1)
    assert(gameSession.getTerrain(gameSession.getPlayerPosition(1)) == probabilityTerrain)

    val behaviour = gameSession.getTerrain(gameSession.getPlayerPosition(1)).getBehaviourExplorer(1)
    behaviour.next()

    assert(gameSession.gameBank.getMoneyForPlayer(1) == 700)
  }

  test("removing money from player 1, giving one card of probability") {
    gameSession.startGame()
    val removeMoneyStory: EventStory = EventStory("Test", "Remove 500 money")
    val removeMoneyStrategy: EventStrategy = id => gameSession.gameBank.makeTransaction(id, amount = 500)
    val removeMoney = DefaultCards(EventGroup(Event(removeMoneyStory, removeMoneyStrategy)), "remove money")
    probabilityTerrain.addCards(removeMoney)

    gameSession.gameBank.makeTransaction(receiverId = 1, 500)
    assert(gameSession.gameBank.getMoneyForPlayer(1) == 700)
    assert(gameSession.getPlayerPosition(1) == 0)
    gameSession.movePlayer(1, steps = 1)
    assert(gameSession.getPlayerPosition(1) == 1)
    assert(gameSession.getTerrain(gameSession.getPlayerPosition(1)) == probabilityTerrain)

    val behaviour = gameSession.getTerrain(gameSession.getPlayerPosition(1)).getBehaviourExplorer(1)
    behaviour.next()

    assert(gameSession.gameBank.getMoneyForPlayer(1) == 200)
  }

  test("player have to do one entire lap to get an extra bonus") {
    gameSession.startGame()

    val doOneLapStory: EventStory = EventStory("Test", "Do One Lap and stop at the start cell")
    val doOneLapStrategy: EventStrategy = id =>
      gameSession.movePlayer(
        id,
        steps = (gameSession.gameStore.getNumberOfTerrains(_ => true) - gameSession.getPlayerPosition(id)) + 1
      )
    val doOneLap = DefaultCards(EventGroup(Event(doOneLapStory, doOneLapStrategy)), "do one lap")
    probabilityTerrain.addCards(doOneLap)

    gameSession.movePlayer(1, steps = 1)
    assert(gameSession.getPlayerPosition(1) == 1)
    assert(gameSession.getTerrain(gameSession.getPlayerPosition(1)) == probabilityTerrain)

    val behaviour = gameSession.getTerrain(gameSession.getPlayerPosition(1)).getBehaviourExplorer(1)
    behaviour.next()

    assert(gameSession.gameBank.getMoneyForPlayer(1) == 400)
  }

  test("testing some surprises cards") {
    gameSession.startGame()
    gameSession.movePlayer(1, steps = 2)
    assert(gameSession.getPlayerPosition(1) == 2)
    assert(gameSession.getTerrain(gameSession.getPlayerPosition(1)) == surpriseTerrain)
    val behaviour = gameSession.getTerrain(gameSession.getPlayerPosition(1)).getBehaviourExplorer(1)
    behaviour.next()
  }
