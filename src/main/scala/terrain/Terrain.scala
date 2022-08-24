package terrain

object Terrain :

  trait Behaviour

  /**
   * It encapsulates the information that a Terrain needs
   */
  trait BasicInfo:
    def name: String
    def position: Int
    def behaviour: Behaviour

  /**
   * Simple implementation of BasicInfo
   * @param name the name of the terrain
   * @param position the position that the terrain has in the game
   * @param behaviour the behaviour that the terrain has in the game
   */
  case class TerrainInfo(override val name: String, override val position: Int, override val behaviour: Behaviour) extends BasicInfo

  /**
   * It represents the basic functionalities of a Terrain
   */
  trait Terrain:
    /**
     * @return the terrain's information
     */
    def basicInfo: BasicInfo
    /**
     * It activates the terrain's behaviour
     * @return
     */
    def triggerBehaviour(): Any

  /**
   * Simple implementation of Terrain
   * @param basicInfo is the set of information a terrain needs
   */
  case class BasicTerrain(override val basicInfo: BasicInfo) extends Terrain:
    override def triggerBehaviour(): Any = "test behaviour"