package terrain.card

import behaviour.BehaviourModule.Behaviour
import behaviour.event.EventGroup
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

  val t: Terrain = Terrain(TerrainInfo("carta probabilità", 1), Behaviour(EventGroup(Seq())))
  val t1: Terrain = Terrain(TerrainInfo("carta imprevisti", 1), Behaviour())

  val probabilityTerrain: CardTerrain = CardTerrain.apply(t, gameSession, false)
  val surpriseTerrain: CardTerrain = CardTerrain.apply(t1, gameSession, true)
