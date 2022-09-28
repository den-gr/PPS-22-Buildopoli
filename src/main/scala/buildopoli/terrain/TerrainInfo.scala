package buildopoli.terrain

import buildopoli.behaviour.BehaviourModule.Behaviour

/** It encapsulates the information that a Terrain needs
  */
trait TerrainInfo:
  /** The name of the terrain
    * @return
    */
  def name: String

object TerrainInfo:

  /** A factory to create a simple implementation of information
    * @param name
    *   the name of the terrain
    */
  def apply(name: String): TerrainInfo = TerrainInfoImpl(name)

  private case class TerrainInfoImpl(override val name: String) extends TerrainInfo
