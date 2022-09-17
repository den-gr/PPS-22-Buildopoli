package lib.terrain

/** It represents the token used in the game that can be built on a buildable terrain to increase the rent
  */
trait Token:
  /** @return
    *   the names given to the used token
    */
  def tokenNames: Seq[String]

  /** @param name
    *   of the token
    * @return
    *   the maximum number of the specified token that can be built on a terrain
    */
  def maxNumToken(name: String): Int

  /** @param name
    *   of the token
    * @return
    *   the buying price of the specified token
    */
  def buyingPrice(name: String): Int

  /** @param name
    *   of the token
    * @return
    *   the Seq with the single bonus provided by each token
    */
  def totalBonusPrice(name: String): Seq[Int]

  def listAvailableToken(): Seq[String]

  /** It is used to add an amount of the specific token
    * @param name
    *   of the token
    * @param num
    *   number of the tokens
    * @return
    *   a new Token object
    */
  def addToken(name: String, num: Int): Token

  /** It is used to remove an amount of the specific token
    * @param name
    *   of the token
    * @param num
    *   number of the tokens
    * @return
    *   a new Token object
    */
  def removeToken(name: String, num: Int): Token

  /** @param name
    *   of the token
    * @return
    *   the number of tokens with the specified name
    */
  def getNumToken(name: String): Int

object Token:

  def apply(levels: Seq[String], maxNumToken: Seq[Int], rentBonuses: Seq[Seq[Int]], buyingPrices: Seq[Int]): Token =
    TokenWithBonus(levels, maxNumToken, rentBonuses, buyingPrices, List.fill(levels.size)(0))

  private case class TokenWithBonus(
      private val levels: Seq[String],
      private val maxNumToken: Seq[Int],
      private val rentBonuses: Seq[Seq[Int]],
      private val buyingPrices: Seq[Int],
      private val numToken: Seq[Int]
  ) extends Token:

    if levels.size != levels.distinct.size then throw Exception("Levels must have different names!!!")
    if !(levels.size == maxNumToken.size && levels.size == rentBonuses.size && rentBonuses.size == buyingPrices.size && rentBonuses.size == numToken.size)
    then throw Exception("Arrays size do not match!!!")
    for l <- levels do
      if maxNumToken(l) != totalBonusPrice(l).size then throw Exception("Arrays size or map size do not match!!!")

    override def tokenNames: Seq[String] = levels
    override def listAvailableToken(): Seq[String] = tokenNames filter (l => canBuildToken(l))
    override def maxNumToken(name: String): Int = maxNumToken(fromLevelNameToNumber(name))
    override def buyingPrice(name: String): Int = buyingPrices(fromLevelNameToNumber(name))
    override def totalBonusPrice(name: String): Seq[Int] = rentBonuses(fromLevelNameToNumber(name))
    override def getNumToken(name: String): Int = numToken(fromLevelNameToNumber(name))
    override def addToken(name: String, num: Int): Token = num > 0 && getNumToken(name) + num <= maxNumToken(name) match
      case true =>
        (fromLevelNameToNumber(name), getNumToken(name)) match
          case (l, n) if l == 0 => changeToken(name, num)
          case (l, n) if l > 0 && getNumToken(previousLevel(l)) == maxNumToken(previousLevel(l)) =>
            changeToken(name, num).removeToken(previousLevel(l), maxNumToken(previousLevel(l)))

    override def removeToken(name: String, num: Int): Token = num > 0 && getNumToken(name) - num >= 0 match
      case true => changeToken(name, -num)

    private def fromLevelNameToNumber(name: String): Int = levels.indexOf(name)
    private def previousLevel(level: Int): String = levels(level - 1)
    private def changeToken(name: String, num: Int): Token =
      copy(numToken = levels map (n => if n == name then getNumToken(n) + num else getNumToken(n)))
    private def isFull(name: String): Boolean = maxNumToken(name) == getNumToken(name)
    private def areHigherLevelsEmpty: Boolean =
      !(tokenNames exists (n => fromLevelNameToNumber(n) != 0 && getNumToken(n) > 0))
    private def canBuildToken(name: String): Boolean =
      val l = fromLevelNameToNumber(name)
      (l == 0 && !isFull(levels.head) && areHigherLevelsEmpty) || (l != 0 && isFull(previousLevel(l)))
