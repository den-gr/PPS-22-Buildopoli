package lib.endGame

import lib.endGame.EndGame.NoMoneyNoTerrains
import lib.gameManagement.gameStore.GameStore
import lib.player.PlayerImpl
import org.scalatest.BeforeAndAfterEach
import org.scalatest.funsuite.AnyFunSuite
import lib.gameManagement.gameStore.*
import lib.player.*
import lib.terrain.Terrain
import lib.terrain.TerrainInfo
import lib.terrain.Purchasable
import lib.terrain.PurchasableState
import lib.gameManagement.gameBank.*
class EndGameTest extends AnyFunSuite with BeforeAndAfterEach:

  var gs: GameStore = _
  var p1: Player = _
  var eg: EndGame = _
  var t1: Purchasable = _
  var b: Bank = _

  override def beforeEach(): Unit =
    gs = GameStoreImpl()
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
