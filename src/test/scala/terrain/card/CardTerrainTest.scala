package terrain.card

import behaviour.BehaviourModule.Behaviour
import behaviour.event.EventGroup
import behaviour.event.EventModule.{Event, EventStrategy}
import behaviour.event.EventStoryModule.EventStory
import gameManagement.gameBank.{Bank, GameBankImpl}
import gameManagement.gameOptions.GameOptions
import gameManagement.gameSession.{GameSession, GameSessionImpl}
import gameManagement.gameStore.{GameStore, GameStoreImpl}
import gameManagement.gameTurn.{DefaultGameTurn, GameTurn}
import lap.Lap
import lap.Lap.MoneyReward
import org.scalatest.funsuite.AnyFunSuite
import player.Player
import terrain.{Terrain, TerrainInfo}

class CardTerrainTest extends AnyFunSuite:

  val selector: (Seq[Player], Seq[Int]) => Int =
    (playerList: Seq[Player], playerWithTurn: Seq[Int]) =>
      playerList.filter(el => !playerWithTurn.contains(el.playerId)).head.playerId

  val gameStore: GameStore = GameStoreImpl()
  val gameOptions: GameOptions = GameOptions(200, 2, 10, 6, selector)
  val gameBank: Bank = GameBankImpl(gameOptions, gameStore)
  val gameTurn: GameTurn = DefaultGameTurn(gameOptions, gameStore)
  val gameLap: Lap = Lap(MoneyReward(200, gameBank))

  val gameSession: GameSession = GameSessionImpl(gameOptions, gameBank, gameTurn, gameStore, gameLap)

  val t: Terrain = Terrain(TerrainInfo("carta probabilità", 1), Behaviour())
  val t1: Terrain = Terrain(TerrainInfo("carta imprevisti", 2), Behaviour())

  val probabilityTerrain: CardTerrain = CardTerrain.apply(t)
  val surpriseTerrain: CardTerrain = CardTerrain.apply(t1, gameSession, true)

  test("adding 4 players to gameSession") {
    gameSession.addManyPlayers(4)
    gameSession.gameStore.putTerrain(probabilityTerrain, surpriseTerrain)
    gameStore.startGame()

    assert(gameSession.gameStore.terrainList.size == 2)
    assert(gameSession.gameStore.playersList.size == 4)
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
    println(behaviour.currentStories)

    behaviour.next()

    assert(gameSession.gameBank.getMoneyForPlayer(1) == 700)
  }
