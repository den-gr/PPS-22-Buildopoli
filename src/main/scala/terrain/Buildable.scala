package terrain

import Purchasable.*
import Terrain.*
import Token.*
import GroupManager.*

object Buildable :

  /**
   *  The properties of a Buildable terrain
   */
  trait Buildable extends Purchasable:
    /**
     * It is used to change the ownership of the terrain
     * @param newOwner the new owner of the terrain
     * @return a new Buildable object
     */
    override def changeOwner(newOwner: Option[Int]): Buildable
    /**
     * It is used to mortgage the purchasable terrain to the bank
     * @return a new Buildable object
     */
    override def mortgage: Buildable
    /**
     * It tells if it is possible to build
     * @param groupManager gives information about the terrain's group
     * @return if it is possible to build
     */
    def canBuild(groupManager: GroupManager): Boolean
    /**
     *
     * @param name of the token
     * @return the price needed to buy the specified token
     */
    def tokenBuyingPrice(name: String): Int
    /**
     *
     * @param name of the token
     * @return the number of tokens with the specified name
     */
    def getNumToken(name: String): Int
    /**
     * It is used to add a token
     * @param name of the token
     * @param num number of tokens to add
     * @return a new Buildable
     */
    def addToken(name:String, num: Int): Buildable
    /**
     * It is used to remove a token
     * @param name of the token
     * @param num number of tokens to add
     * @return a new Buildable
     */
    def destroyToken(name: String, num: Int): Buildable
    /**
     *
     * @param name of the token
     * @return the price at which the token can be sold
     */
    def tokenSellingPrice(name: String): Int

  /**
   * The standard implementation of a buildable terrain
   * @param terrain a terrain that is a purchasable one
   * @param token the chosen token structure
   */
  case class BuildableTerrain(private val terrain: Purchasable, private val token: Token) extends Buildable:

    override val basicInfo: BasicInfo = terrain.basicInfo
    override def triggerBehaviour(): Any = terrain.triggerBehaviour()

    override def price: Int = terrain.price
    override def group: String = terrain.group
    override def state: PurchasableState = terrain.state
    override def owner: Option[Int] = terrain.owner

    override def changeOwner(newOwner: Option[Int]): Buildable = BuildableTerrain(terrain.changeOwner(newOwner), token)
    override def computeTotalRent(gm: GroupManager): Int =
      var numToken: Int = 0
      token.tokenNames.foreach(tn => numToken = numToken + token.getNumToken(tn))
      numToken > 0 match
        case true => var total: Int = 0; token.tokenNames.foreach(tn => total = total + token.totalBonusPrice(tn).take(token.getNumToken(tn)).sum); total
        case false => terrain.computeTotalRent(gm)

    override def mortgage: Buildable = BuildableTerrain(terrain.mortgage, token)
    override def computeMortgage: Int = terrain.computeMortgage

    override def canBuild(groupManager: GroupManager): Boolean = owner.nonEmpty && groupManager.isGroupComplete(owner.get, group)
    override def getNumToken(name: String): Int = token.getNumToken(name)
    override def addToken(name: String, num: Int):Buildable = BuildableTerrain(terrain, token.addToken(name, num))
    override def destroyToken(name: String, num: Int): Buildable = BuildableTerrain(terrain, token.removeToken(name, num))
    override def tokenBuyingPrice(name: String): Int = token.buyingPrice(name)
    override def tokenSellingPrice(name: String): Int = token.buyingPrice(name)/2 
