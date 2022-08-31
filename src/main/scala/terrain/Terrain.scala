package terrain

import behaviour.BehaviourModule.Behaviour

/**
 * It represents the basic functionalities of a Terrain
 */
trait Terrain:
  /**
   * @return the terrain's information
   */
  def basicInfo: TerrainInfo
  /**
   * It activates the terrain's behaviour
   * @return
   */
  def triggerBehaviour(): Any

object Terrain :

  /**
   * A factory to create a simple implementation of Terrain
   * @param basicInfo is the set of information a terrain needs
   */
  def apply(basicInfo: TerrainInfo): Terrain = BasicTerrain(basicInfo)

  private case class BasicTerrain(override val basicInfo: TerrainInfo) extends Terrain:
    override def triggerBehaviour(): Any = "test behaviour"

