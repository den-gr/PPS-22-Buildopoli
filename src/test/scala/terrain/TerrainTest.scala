package terrain

import org.scalatest.funsuite.AnyFunSuite
import Terrain.*
import behaviour.BehaviourModule.Behaviour
import behaviour.event.EventGroup
import behaviour.event.EventModule.Event
import behaviour.event.EventStoryModule.EventStory
import behaviour.factory.BehaviourFactory
import gameManagement.gameSession.GameSession
import player.Player
import util.GameSessionHelper

class TerrainTest extends AnyFunSuite:

  val p: Player = Player(10)
  val e: Event = Event(EventStory("Test", "Add 500 money"), eventStrategy = id => p.setPlayerMoney(p.getPlayerMoney + 500))
  val b: Behaviour = Behaviour(e)

  val t: Terrain = Terrain(TerrainInfo("tassa di lusso", 1), b)

  test("A terrain has a name") {
    assert(t.basicInfo.name == "tassa di lusso")
  }
  test("A terrain has a position") {
    assert(t.basicInfo.position == 1)
  }
  test("A terrain can trigger a behaviour") {
    p.setPlayerMoney(500)
    assert(p.getPlayerMoney == 500)
    t.triggerBehaviour(10).next(0,0)
    assert(p.getPlayerMoney == 1000)
  }
