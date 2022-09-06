package terrain.card

import terrain.*

trait CardTerrain extends Terrain:
  var cardList: List[Card]

object CardTerrain:
  def apply(): CardTerrain = ???
