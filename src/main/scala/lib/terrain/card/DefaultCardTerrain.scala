package lib.terrain.card

import lib.behaviour.BehaviourExplorer
import lib.terrain.{Terrain, TerrainInfo}

import scala.util.Random

case class DefaultCardTerrain(terrain: Terrain) extends CardTerrain:
  export terrain.basicInfo

  private var cards: List[Card] = List()
  private val randomGenerator: Random = new Random()

  def cardList: List[Card] = cards

  def cardList_=(list: List[Card]): Unit = this.cards = list

  override def addCards(card: Card*): Unit =
    cards = cards :++ card

  override def removeCard(name: String): Unit = cards = cards.filterNot(el => el.name == name)

  override def getCard(name: String): Card = cards.filter(el => el.name == name).head

  def getCasualCardFromList: Card =
    if cards.size == 1 then cards.head
    else cards.apply(randomGenerator.nextInt(cards.size))

  def exchangeElement(card: Card): Unit =
    removeCard(card.name)
    addCards(card)

  override def getBehaviourExplorer(playerID: Int): BehaviourExplorer =
    val card: Card = getCasualCardFromList
    exchangeElement(card)
    BehaviourExplorer.apply(terrain.getBehaviourExplorer(playerID), card.consequences)
