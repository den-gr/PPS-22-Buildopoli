package lib.terrain

import Purchasable.*
import Terrain.*
import Token.*
import GroupManager.*
import lib.behaviour.BehaviourIterator
import lib.behaviour.BehaviourModule.Behaviour

/** The properties of a Buildable terrain
  */
trait Buildable extends Purchasable:

  /** It tells if it is possible to build
    * @param groupManager
    *   gives information about the terrain's group
    * @return
    *   if it is possible to build
    */
  def canBuild(groupManager: GroupManager): Boolean

  /** @param name
    *   of the token
    * @return
    *   the price needed to buy the specified token
    */
  def tokenBuyingPrice(name: String): Int

  /** @param name
    *   of the token
    * @return
    *   the number of tokens with the specified name
    */
  def getNumToken(name: String): Int

  /** It is used to add a token
    * @param name
    *   of the token
    * @param num
    *   number of tokens to add
    */
  def addToken(name: String, num: Int): Unit

  /** It is used to remove a token
    * @param name
    *   of the token
    * @param num
    *   number of tokens to add
    */
  def destroyToken(name: String, num: Int): Unit

  /** @param name
    *   of the token
    * @return
    *   the price at which the token can be sold
    */
  def tokenSellingPrice(name: String): Int

object Buildable:

  /** A factory to create a buildable terrain
    * @param terrain
    *   a terrain that is a purchasable one
    * @param token
    *   the chosen token structure
    */
  def apply(terrain: Purchasable, token: Token): Buildable = BuildableTerrain(terrain, token)

  private case class BuildableTerrain(private val terrain: Purchasable, private var token: Token) extends Buildable:

    export terrain.{computeTotalRent as _, *}

    override def computeTotalRent(gm: GroupManager): Int =
      var numToken: Int = 0
      token.tokenNames.foreach(tn => numToken = numToken + token.getNumToken(tn))
      numToken > 0 match
        case true =>
          var total: Int = 0;
          token.tokenNames.foreach(tn => total = total + token.totalBonusPrice(tn).take(token.getNumToken(tn)).sum);
          total
        case false => terrain.computeTotalRent(gm)
    override def canBuild(groupManager: GroupManager): Boolean =
      owner.nonEmpty && groupManager.isGroupComplete(owner.get, group)
    override def getNumToken(name: String): Int = token.getNumToken(name)
    override def addToken(name: String, num: Int): Unit = token = token.addToken(name, num)
    override def destroyToken(name: String, num: Int): Unit = token = token.removeToken(name, num)
    override def tokenBuyingPrice(name: String): Int = token.buyingPrice(name)
    override def tokenSellingPrice(name: String): Int = token.buyingPrice(name) / 2
