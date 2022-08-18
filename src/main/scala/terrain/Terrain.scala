package terrain

object Terrain :

  trait Behaviour

  trait BasicInfo:
    def name: String
    def position: Int
    def behaviour: Behaviour

  case class TerrainInfo(override val name: String, override val position: Int, override val behaviour: Behaviour) extends BasicInfo

  //The basic functionalities of a Terrain
  trait Terrain:
    def basicInfo: BasicInfo
    def triggerBehaviour(): Any

  case class BasicTerrain(override val basicInfo: BasicInfo) extends Terrain:
    override def triggerBehaviour(): Any = "test behaviour"