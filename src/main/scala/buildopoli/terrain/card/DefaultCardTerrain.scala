package buildopoli.terrain.card

import buildopoli.behaviour.BehaviourExplorer
import buildopoli.behaviour.BehaviourModule.Behaviour
import buildopoli.terrain.{Terrain, TerrainInfo}

import scala.util.Random

private case class DefaultCardTerrain(terrain: Terrain) extends CardTerrain:
  export terrain.{basicInfo}

  private var cards: List[Card] = List()
  private val randomGenerator: Random = new Random()

  def cardList: List[Card] = cards

  def cardList_=(list: List[Card]): Unit = this.cards = list

  override def addCards(card: Card*): Unit =
    cards = cards :++ card

  override def removeCard(name: String): Unit = cards = cards.filterNot(el => el.name == name)

  def getCasualCardFromList: Card =
    if cards.size == 1 then cards.head
    else cards(randomGenerator.nextInt(cards.size))

  def exchangeElement(card: Card): Unit =
    removeCard(card.name)
    addCards(card)

  override def behaviour: Behaviour =
    val card: Card = getCasualCardFromList
    exchangeElement(card)
    terrain.behaviour.addEventGroups(Seq(card.consequences))
