package buildopoli.terrain

import org.scalatest.funsuite.AnyFunSuite
import buildopoli.terrain.Terrain.*
import buildopoli.behaviour.BehaviourModule.Behaviour
import buildopoli.behaviour.event.EventModule.Event
import buildopoli.behaviour.event.story.EventStoryModule.EventStory
import buildopoli.behaviour.event.EventGroup
import buildopoli.behaviour.factory.BehaviourFactory
import buildopoli.gameManagement.gameSession.GameSession
import buildopoli.player.Player
import buildopoli.terrain.{Terrain, TerrainInfo}
import buildopoli.util.GameSessionHelper

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
    t.behaviour.getBehaviourExplorer(10).next()
    assert(p.getPlayerMoney == 1000)
  }
