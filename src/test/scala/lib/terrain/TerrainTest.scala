package lib.terrain

import org.scalatest.funsuite.AnyFunSuite
import lib.terrain.Terrain.*
import lib.behaviour.BehaviourModule.Behaviour
import lib.behaviour.event.EventModule.Event
import lib.behaviour.event.EventStoryModule.EventStory
import lib.behaviour.event.EventGroup
import lib.behaviour.factory.BehaviourFactory
import lib.gameManagement.gameSession.GameSession
import lib.player.Player
import lib.terrain.{Terrain, TerrainInfo}
import lib.util.GameSessionHelper

class TerrainTest extends AnyFunSuite:

  val p: Player = Player(10)
  val e: Event =
    Event(EventStory("Test", "Add 500 money"), eventStrategy = id => p.setPlayerMoney(p.getPlayerMoney + 500))
  val b: Behaviour = Behaviour(e)

  val t: Terrain = Terrain(TerrainInfo("tassa di lusso"), b)

  test("A terrain has a name") {
    assert(t.basicInfo.name == "tassa di lusso")
  }

  test("A terrain can trigger a behaviour") {
    p.setPlayerMoney(500)
    assert(p.getPlayerMoney == 500)
    t.getBehaviourIterator(10).next(0, 0)
    assert(p.getPlayerMoney == 1000)
  }
