package buildopoli.terrain

import buildopoli.behaviour.BehaviourExplorer
import buildopoli.behaviour.BehaviourModule.Behaviour

/** It represents the basic functionalities of a Terrain
  */
trait Terrain:
  /** @return
    *   the terrain's information
    */
  def basicInfo: TerrainInfo

  /** @return
    *   the behaviour object of the terrain
    */
  def behaviour: Behaviour

object Terrain:

  /** A factory to create a simple implementation of Terrain
    * @param basicInfo
    *   is the set of information a terrain needs
    */
  def apply(basicInfo: TerrainInfo, behaviour: Behaviour): Terrain = BasicTerrain(basicInfo, behaviour)

  private case class BasicTerrain(override val basicInfo: TerrainInfo, override val behaviour: Behaviour)
      extends Terrain
