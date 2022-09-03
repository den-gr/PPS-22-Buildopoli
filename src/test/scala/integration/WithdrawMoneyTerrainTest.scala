package integration

import behaviour.BehaviourModule.Behaviour
import behaviour.event.EventFactory
import behaviour.event.EventStoryModule.EventStory
import gameManagement.gameBank.Bank
import org.scalatest.BeforeAndAfterEach
import org.scalatest.funsuite.AnyFunSuite
import terrain.{Terrain, TerrainInfo}
import util.GameSessionHelper.DefaultGameSession

class WithdrawMoneyTerrainTest extends AnyFunSuite with BeforeAndAfterEach:
  private val PLAYER_1: Int = 1
  private var bank: Bank = _
  private var behaviour: Behaviour = _
  private val AMOUNT = 100
  private val story: EventStory = EventStory(s"Player lose $AMOUNT money", "Ok")

  private val gameSession = DefaultGameSession()
  bank = gameSession.gameBank
  behaviour = Behaviour(EventFactory(gameSession).WithdrawMoneyEvent(story, AMOUNT))

  private val terrain = Terrain(TerrainInfo("You lose 100 money", 1, behaviour))

//  test("check "){
//    val it = terrain.triggerBehaviour(PLAYER_1)
//    gameSession.gameStore.terrainList.
//  }
