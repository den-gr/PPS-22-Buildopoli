package terrain

trait CardTerrain extends Terrain:
  var cardList: List[Card]


object CardTerrain:
  def apply(): CardTerrain = Surprises()

class abstractCard() extends CardTerrain:
  private var cards: List[Card] = List()

  def cardList: List[Card] = cardList
  def cardList_=(list: List[Card]): Unit = this.cards = list


case class Surprises() extends abstractCard:


