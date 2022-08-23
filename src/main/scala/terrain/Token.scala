package terrain

object Token :
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
    def maxNumToken(name:String): Int
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

  case class TokenWithBonus(tokenTypeToRentBonus: Map[String, Seq[Int]], maxValues: Seq[Int], buyingPrices: Seq[Int], numToken: Map[String, Int]) extends Token:

    private val maxToken: Map[String, Int] = (tokenTypeToRentBonus.keys zip maxValues).toMap
    private var check: Boolean = tokenTypeToRentBonus.keys.equals(numToken.keys)
    check match {case false => throw Exception("Map keys are not the same") ; case _ => }
    check = check && tokenTypeToRentBonus.size == maxValues.size && maxValues.size == buyingPrices.size && buyingPrices.size == numToken.size
    for i <- tokenTypeToRentBonus.keys do
      check = check && maxToken(i) == tokenTypeToRentBonus(i).size
    check match {case false => throw Exception("Arrays size or map size do not match!!!") case true => }
    private val buyingPricesList: Map[String, Int] = (tokenTypeToRentBonus.keys zip buyingPrices).toMap

    override def tokenNames: Seq[String] = tokenTypeToRentBonus.keys.toList
    override def maxNumToken(name: String): Int = maxToken(name)
    override def buyingPrice(name: String): Int = buyingPricesList(name)
    override def totalBonusPrice(name: String): Seq[Int] = tokenTypeToRentBonus(name)
    override def getNumToken(name: String): Int = numToken(name)
    override def addToken(name: String, num: Int): Token = num > 0 && getNumToken(name) + num <= maxToken(name) match
      case true => changeToken(name, num)
    override def removeToken(name:String, num: Int): Token = num > 0 && getNumToken(name) - num >= 0 match
      case true => changeToken(name, -num)

    private def changeToken(name: String, num:Int): Token =
      TokenWithBonus(tokenTypeToRentBonus, maxValues, buyingPrices, numToken.map((k, v) => k match
        case `name` => (k, v + num)
        case _ => (k, v)
      ))