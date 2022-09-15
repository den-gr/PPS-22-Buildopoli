package lib.terrain

import lib.behaviour.BehaviourModule.Behaviour

/** It encapsulates the information that a Terrain needs
  */
trait TerrainInfo:
  def name: String

object TerrainInfo:

  /** A factory to create a simple implementation of BasicInfo
    * @param name
    *   the name of the terrain
    */
  def apply(name: String): TerrainInfo = TerrainInfoImpl(name)

  private case class TerrainInfoImpl(override val name: String) extends TerrainInfo
