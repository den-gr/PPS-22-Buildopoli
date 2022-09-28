package buildopoli.endGame

import buildopoli.endGame.EndGame.NoMoneyNoTerrains
import buildopoli.gameManagement.gameStore.GameStore
import buildopoli.player.PlayerImpl
import org.scalatest.BeforeAndAfterEach
import org.scalatest.funsuite.AnyFunSuite
import buildopoli.gameManagement.gameStore.*
import buildopoli.player.*
import buildopoli.terrain.Terrain
import buildopoli.terrain.TerrainInfo
import buildopoli.terrain.Purchasable
import buildopoli.terrain.PurchasableState
import buildopoli.gameManagement.gameBank.*
class EndGameTest extends AnyFunSuite with BeforeAndAfterEach:

  var gs: GameStore = _
  var p1: Player = _
  var eg: EndGame = _
  var t1: Purchasable = _
  var b: Bank = _

  override def beforeEach(): Unit =
    gs = GameStore()
    gs.addPlayer()
    gs.addPlayer()
    gs.playersList.head.setPlayerMoney(500)
    t1 = Purchasable(Terrain(TerrainInfo("trial"), null), 500, "1", null, null, Option(2))
    gs.terrainList = Seq(t1)
    b = GameBankImpl(gs)

    eg = NoMoneyNoTerrains()

  test("A player with no terrains but money does not lose, as well as a player that owns terrains but has no money") {
    assert(gs.playersList.size == 2)
    eg.deleteDefeatedPlayer(p => EndGame.defeatedForNoMoneyAndNoTerrainsOwned(p, gs, b), gs)
    assert(gs.playersList.size == 2)
  }

  test("In a game session a player can lose if he has no more terrains and money") {
    assert(gs.playersList.size == 2)
    t1 changeOwner None
    eg.deleteDefeatedPlayer(p => EndGame.defeatedForNoMoneyAndNoTerrainsOwned(p, gs, b), gs)
    assert(gs.playersList.size == 1)
    gs.playersList.head.setPlayerMoney(0)
    eg.deleteDefeatedPlayer(p1 => EndGame.defeatedForNoMoneyAndNoTerrainsOwned(p1, gs, b), gs)
    assert(gs.playersList.isEmpty)
  }

  test("It is possible to know the id of the defeated players") {
    assert(eg.deleteDefeatedPlayer(p => EndGame.defeatedForNoMoneyAndNoTerrainsOwned(p, gs, b), gs).toSet == Set())
    t1 changeOwner None
    gs.playersList.head.setPlayerMoney(0)
    assert(eg.deleteDefeatedPlayer(p => EndGame.defeatedForNoMoneyAndNoTerrainsOwned(p, gs, b), gs).toSet == Set(1, 2))
  }

  test("When a player is defeated his terrains are given to the bank") {
    eg.deleteDefeatedPlayer(p => EndGame.defeatedForNoMoneyAndNoTerrainsOwned(p, gs, b), gs)
    assert(gs.playersList.size == 2)
    t1.mortgage()
    assert(t1.state == PurchasableState.MORTGAGED)
    eg.deleteDefeatedPlayer(p => EndGame.defeatedForNoMoneyAndNoTerrainsOwned(p, gs, b), gs)
    assert(gs.playersList.size == 1)
    assert(t1.state == PurchasableState.IN_BANK)
    assert(t1.owner.isEmpty)
  }

  test("A player is defeated if he is in debit") {
    eg.deleteDefeatedPlayer(p => EndGame.defeatedForNoMoneyAndNoTerrainsOwned(p, gs, b), gs)
    assert(gs.playersList.size == 2)
    b.makeTransaction(1, amount = 1000)
    eg.deleteDefeatedPlayer(p => EndGame.defeatedForNoMoneyAndNoTerrainsOwned(p, gs, b), gs)
    assert(gs.playersList.size == 1)

  }
