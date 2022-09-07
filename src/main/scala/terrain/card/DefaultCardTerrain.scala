package terrain.card
import behaviour.BehaviourIterator
import terrain.{Terrain, TerrainInfo}

case class DefaultCardTerrain(terrain: Terrain) extends CardTerrain:
  export terrain.basicInfo

  private var cards: List[Card] = List()

  def cardList: List[Card] = cardList

  def cardList_=(list: List[Card]): Unit = this.cards = list

  override def addCards(card: Card*): Unit = cards = cards :++ card

  override def removeCard(name: String): Unit = cards.filterNot(el => el.name == name)

  override def getCard(name: String): Card = cards.filter(el => el.name == name).head

  // prima devo prendere un card (in modo automatico)
  // e ci appiccico l'event group della carta pescata
  // metodo per appicciare un event group all'iteratore
  // TODO
  override def getBehaviourIterator(playerID: Int): BehaviourIterator =
    val hit = terrain.getBehaviourIterator(playerID)
    hit
