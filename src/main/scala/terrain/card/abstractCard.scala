package terrain.card

abstract class abstractCard() extends CardTerrain:
  private var cards: List[Card] = List()

  def cardList: List[Card] = cardList
  def cardList_=(list: List[Card]): Unit = this.cards = list
