package terrain

import Purchasable.*
import Terrain.*

object Buildable :

  trait Token:
    def tokenNames: Seq[String]
    def maxToken(name:String): Int
    def buyingPrice(name:String): Int
    def totalBonusPrice(name:String): Seq[Int]
    def addToken(name: String, num: Int): Token
    def removeToken(name: String, num: Int): Token
    def getNumToken(name: String): Int

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

  //The properties of a Buildable terrain
  trait Buildable extends Purchasable:
    override def changeOwner(newOwner: Option[Int]): Buildable
    override def mortgage: Buildable

    def tokenBuyingPrice(name: String): Int
    def getNumToken(name: String): Int
    def addToken(name:String, num: Int): Buildable
    def destroyToken(name: String, num: Int): Buildable
    def tokenSellingPrice(name: String): Int

  case class BuildableTerrain(private val terrain: Purchasable, private val token: Token) extends Buildable:
    //basic
    override val basicInfo: BasicInfo = terrain.basicInfo
    override def triggerBehaviour(): Any = terrain.triggerBehaviour()

    //property
    override def price: Int = terrain.price
    override def group: String = terrain.group
    override def state: PurchasableState = terrain.state
    override def owner: Option[Int] = terrain.owner

    override def changeOwner(newOwner: Option[Int]): Buildable = BuildableTerrain(terrain.changeOwner(newOwner), token)
    override def computeTotalRent: Int = //TODO sarebbe da spostare fuori nella rent strategy ma come
      var numToken: Int = 0
      token.tokenNames.foreach(tn => numToken = numToken + token.getNumToken(tn))
      numToken > 0 match
        case true => var total: Int = 0; token.tokenNames.foreach(tn => total = total + token.totalBonusPrice(tn).take(token.getNumToken(tn)).sum); total
        case false => terrain.computeTotalRent

    override def mortgage: Buildable = BuildableTerrain(terrain.mortgage, token)
    override def computeMortgage: Int = terrain.computeMortgage

    //buildable
    override def getNumToken(name: String): Int = token.getNumToken(name)
    override def addToken(name: String, num: Int):Buildable = BuildableTerrain(terrain, token.addToken(name, num))
    override def destroyToken(name: String, num: Int): Buildable = BuildableTerrain(terrain, token.removeToken(name, num))
    override def tokenBuyingPrice(name: String): Int = token.buyingPrice(name)
    override def tokenSellingPrice(name: String): Int = token.buyingPrice(name)/2 
