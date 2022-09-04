package terrain

import behaviour.BehaviourModule.Behaviour

/**
 * It encapsulates the information that a Terrain needs
 */
trait TerrainInfo:
  def name: String
  def position: Int

object TerrainInfo :

  /**
   * A factory to create a simple implementation of BasicInfo
   * @param name the name of the terrain
   * @param position the position that the terrain has in the game
   */
  def apply(name: String, position: Int): TerrainInfo = TerrainInfoImpl(name, position)

  private case class TerrainInfoImpl(override val name: String, override val position: Int) extends TerrainInfo



