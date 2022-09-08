package terrain.card

import behaviour.BehaviourIterator
import behaviour.event.EventGroup
import behaviour.event.EventModule.{Event, EventStrategy}
import behaviour.event.EventStoryModule.EventStory
import gameManagement.gameSession.GameSession
import player.Player
import terrain.*

trait CardTerrain extends Terrain:
  var cardList: List[Card]
  def addCards(card: Card*): Unit
  def removeCard(name: String): Unit
  def getCard(name: String): Card

object CardTerrain:

  def apply(terrain: Terrain): CardTerrain = DefaultCardTerrain(terrain: Terrain)

  def apply(terrain: Terrain, gameSession: GameSession, surprises: Boolean): CardTerrain =
    val defaultTerrain = DefaultCardTerrain(terrain)
    surprises match
      case true => defaultTerrain.cardList = createSurpriseCards(gameSession)
      case _ => defaultTerrain.cardList = createProbabilityCards(gameSession)
    defaultTerrain

  def createSurpriseCards(gameSession: GameSession): List[Card] =
    val giveMoneyAllStory: EventStory = EventStory("Test", "Give 500 money to all others")
    val giveMoneyAllStrategy: EventStrategy = id =>
      gameSession.gameBank.makeGlobalTransaction(senderId = id, amount = 500)
    val giveMoneyAll = DefaultCards(EventGroup(Event(giveMoneyAllStory, giveMoneyAllStrategy)), "give money all")

    val goToPrisonStory: EventStory = EventStory("Test", "Go to prison for 2 turns")
    val goToPrisonStrategy: EventStrategy = id => gameSession.gameTurn.lockPlayer(id, 2)
    val goToPrison = DefaultCards(EventGroup(Event(goToPrisonStory, goToPrisonStrategy)), "go to prison")

    val doOneLapWithoutRewardStory: EventStory =
      EventStory("Test", "Do One Lap and stop at the start cell without reward")
    val doOneLapWithoutRewardStrategy: EventStrategy = id =>
      gameSession.setPlayerPosition(id, gameSession.gameOptions.nCells - gameSession.getPlayerPosition(id), false)
    val doOneLapWithoutReward = DefaultCards(
      EventGroup(Event(doOneLapWithoutRewardStory, doOneLapWithoutRewardStrategy)),
      "do one lap without reward"
    )

    List(giveMoneyAll, goToPrison, doOneLapWithoutReward)

  def createProbabilityCards(gameSession: GameSession): List[Card] =
//    val removeMoneyStory: EventStory = EventStory("Test", "Remove 500 money")
//    val removeMoneyStrategy: EventStrategy = id => gameSession.gameBank.makeTransaction(id, amount = 500)
//    val removeMoney = DefaultCards(EventGroup(Event(removeMoneyStory, removeMoneyStrategy)), "remove money")

    val addMoneyStory: EventStory = EventStory("Test", "Add 500 money")
    val addMoneyStrategy: EventStrategy = id => gameSession.gameBank.makeTransaction(receiverId = id, 500)
    val addMoney = DefaultCards(EventGroup(Event(addMoneyStory, addMoneyStrategy)), "add money")

//    val doOneLapStory: EventStory = EventStory("Test", "Do One Lap and stop at the start cell")
//    val doOneLapStrategy: EventStrategy = id =>
//      gameSession.setPlayerPosition(id, gameSession.gameOptions.nCells - gameSession.getPlayerPosition(id))
//    val doOneLap = DefaultCards(EventGroup(Event(doOneLapStory, doOneLapStrategy)), "do one lap")

    List(addMoney)
