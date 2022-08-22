package terrain

import Purchasable.*
import Terrain.*

object Buildable :

  /**
   * It represents the token used in the game that can be built on a buildable terrain to increase the rent
   */
  trait Token:
    /**
     *
     * @return the names given to the used token
     */
    def tokenNames: Seq[String]
    /**
     *
     * @param name of the token
     * @return the maximum number of the specified token that can be built on a terrain
     */
    def maxToken(name:String): Int
    /**
     *
     * @param name of the token
     * @return the buying price of the specified token
     */
    def buyingPrice(name:String): Int
    /**
     *
     * @param name of the token
     * @return the Seq with the single bonus provided by each token
     */
    def totalBonusPrice(name:String): Seq[Int]
    /**
     * It is used to add an amount of the specific token
     * @param name of the token
     * @param num number of the tokens
     * @return a new Token object
     */
    def addToken(name: String, num: Int): Token
    /**
     * It is used to remove an amount of the specific token
     * @param name of the token
     * @param num number of the tokens
     * @return a new Token object
     */
    def removeToken(name: String, num: Int): Token
    /**
     * @param name of the token
     * @return the number of tokens with the specified name
     */
    def getNumToken(name: String): Int

  /**
   * The implementation of Token that relies on two different type of tokens: houses and hotels
   * @param maxHouse the maximum number of houses that can be built
   * @param maxHotel the maximum number of hotels
   * @param housePrice the price needed to buy a house
   * @param hotelPrice the price needed to buy a hotel
   * @param houseRentBonus the sequence of bonus provided by houses
   * @param hotelRentBonus the sequence of bonus provided by hotels
   * @param numHouses the number of houses
   * @param numHotels the number of hotels
   */
  case class HouseHotelToken(maxHouse: Int, maxHotel: Int, housePrice: Int, hotelPrice: Int, houseRentBonus: Seq[Int], hotelRentBonus: Seq[Int],
                            numHouses: Int = 0, numHotels: Int = 0) extends Token:
    private val buyingPricesList: Map[String, Int] = Map(tokenNames.head -> housePrice, tokenNames(1)-> hotelPrice)
    private val maxNumToken: Map[String, Int] = Map(tokenNames.head -> maxHouse, tokenNames(1) -> maxHotel)
    private val bonuses: Map[String, Seq[Int]] = Map(tokenNames.head -> houseRentBonus, tokenNames(1) -> hotelRentBonus)
    private val placedToken: Map[String, Int] = Map(tokenNames.head -> numHouses, tokenNames(1) -> numHotels)

    override def tokenNames: Seq[String] = Array("house", "hotel")
    override def maxToken(name: String): Int = maxNumToken(name)
    override def buyingPrice(name: String): Int = buyingPricesList(name)
    override def totalBonusPrice(name: String): Seq[Int] = bonuses(name)
    override def getNumToken(name: String): Int = placedToken(name)
    override def addToken(name: String, num: Int): Token = num > 0 match
      case true => changeToken(name, num)
    override def removeToken(name:String, num: Int): Token = num > 0 match
      case true => changeToken(name, -num)

    private def changeToken(name: String, num:Int): Token = name match
      case "house" if getNumToken(name) + num <= maxToken(name) => HouseHotelToken(maxHouse, maxHotel, housePrice, hotelPrice, houseRentBonus, hotelRentBonus, numHouses + num, numHotels)
      case "hotel" if getNumToken(name) + num <= maxToken(name) => HouseHotelToken(maxHouse, maxHotel, housePrice, hotelPrice, houseRentBonus, hotelRentBonus, numHouses, numHotels + num)

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
    override def computeTotalRent: Int =
      var numToken: Int = 0
      token.tokenNames.foreach(tn => numToken = numToken + token.getNumToken(tn))
      numToken > 0 match
        case true => var total: Int = 0; token.tokenNames.foreach(tn => total = total + token.totalBonusPrice(tn).take(token.getNumToken(tn)).sum); total
        case false => terrain.computeTotalRent

    override def mortgage: Buildable = BuildableTerrain(terrain.mortgage, token)
    override def computeMortgage: Int = terrain.computeMortgage

    override def getNumToken(name: String): Int = token.getNumToken(name)
    override def addToken(name: String, num: Int):Buildable = BuildableTerrain(terrain, token.addToken(name, num))
    override def destroyToken(name: String, num: Int): Buildable = BuildableTerrain(terrain, token.removeToken(name, num))
    override def tokenBuyingPrice(name: String): Int = token.buyingPrice(name)
    override def tokenSellingPrice(name: String): Int = token.buyingPrice(name)/2 
