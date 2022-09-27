package lib.terrain

import Purchasable.*
import Terrain.*
import Token.*
import GroupManager.*
import lib.behaviour.BehaviourExplorer
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

  /** @return
    *   the names of the tokens the owner can build
    */
  def listAvailableToken(): Seq[String]

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

  /** Given a token name it tells how many token can be built
    * @param name
    *   of the token
    * @return
    *   the number of tokens that can still be built
    */
  def remainingTokens(name: String): Int

object Buildable:

  /** A factory to create a buildable terrain
    * @param terrain
    *   a terrain that is a purchasable one
    * @param token
    *   the chosen token structure
    */
  def apply(terrain: Purchasable, token: Token): Buildable = BuildableTerrain(terrain, token)

  private case class BuildableTerrain(private val terrain: Purchasable, private var token: Token) extends Buildable:

    export terrain.{computeTotalRent as _, mortgage as _, *}

    override def computeTotalRent(gm: GroupManager): Int =
      token.tokenNames.map(n => token.getNumToken(n)).sum > 0 match
        case true => token.tokenNames.map(n => token.totalBonusPrice(n).take(token.getNumToken(n)).sum).sum
        case false => terrain.computeTotalRent(gm)

    override def mortgage(): Unit =
      terrain.mortgage()
      for n <- token.tokenNames if getNumToken(n) > 0 do destroyToken(n, getNumToken(n))

    override def canBuild(groupManager: GroupManager): Boolean =
      owner.nonEmpty && groupManager.isGroupComplete(owner.get, group) && token.listAvailableToken().nonEmpty
    override def getNumToken(name: String): Int = token.getNumToken(name)
    override def addToken(name: String, num: Int): Unit = token = token.addToken(name, num)
    override def destroyToken(name: String, num: Int): Unit = token = token.removeToken(name, num)
    override def tokenBuyingPrice(name: String): Int = token.buyingPrice(name)
    override def tokenSellingPrice(name: String): Int = token.buyingPrice(name) / 2
    override def listAvailableToken(): Seq[String] = token.listAvailableToken()
    override def remainingTokens(name: String): Int = token.maxNumToken(name) - getNumToken(name)
