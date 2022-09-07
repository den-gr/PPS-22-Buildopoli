package terrain.card

import behaviour.BehaviourIterator
import terrain.*

trait CardTerrain extends Terrain:
  var cardList: List[Card]
  def addCards(card: Card*): Unit
  def removeCard(name: String): Unit
  def getCard(name: String): Card

object CardTerrain:
  def apply(terrain: Terrain): CardTerrain = DefaultCardTerrain(terrain: Terrain)
