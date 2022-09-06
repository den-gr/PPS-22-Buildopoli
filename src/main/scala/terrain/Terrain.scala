package terrain

import behaviour.BehaviourIterator
import behaviour.BehaviourModule.Behaviour

/** It represents the basic functionalities of a Terrain
  */
trait Terrain:
  /** @return
    *   the terrain's information
    */
  def basicInfo: TerrainInfo

  /** @return
    *   the object representing the correct sequence of events that need to be triggered
    */
  def getBehaviourIterator(playerID: Int): BehaviourIterator

object Terrain:

  /** A factory to create a simple implementation of Terrain
    * @param basicInfo
    *   is the set of information a terrain needs
    */
  def apply(basicInfo: TerrainInfo, behaviour: Behaviour): Terrain = BasicTerrain(basicInfo, behaviour)

  private case class BasicTerrain(override val basicInfo: TerrainInfo, private val behaviour: Behaviour)
      extends Terrain:
    export behaviour.getBehaviourIterator
