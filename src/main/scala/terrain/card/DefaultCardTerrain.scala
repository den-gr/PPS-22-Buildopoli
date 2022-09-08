package terrain.card
import behaviour.BehaviourIterator
import terrain.{Terrain, TerrainInfo}

import scala.util.Random

case class DefaultCardTerrain(terrain: Terrain) extends CardTerrain:
  export terrain.basicInfo

  private var cards: List[Card] = List()
  private val randomGenerator: Random = new Random()

  def cardList: List[Card] = cardList

  def cardList_=(list: List[Card]): Unit = this.cards = list

  override def addCards(card: Card*): Unit =
    cards = cards :++ card

  override def removeCard(name: String): Unit = cards.filterNot(el => el.name == name)

  override def getCard(name: String): Card = cards.filter(el => el.name == name).head

  def getCasualCardFromList: Card =
    cards.head
      // cards.apply(randomGenerator.nextInt(cards.size) + 1)

  def exchangeElement(card: Card): Unit =
    removeCard(card.name)
    addCards(card)

  override def getBehaviourIterator(playerID: Int): BehaviourIterator =
    val card: Card = getCasualCardFromList
    exchangeElement(card)
    BehaviourIterator.apply(terrain.getBehaviourIterator(playerID), card.consequences)
