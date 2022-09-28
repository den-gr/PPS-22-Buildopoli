package buildopoli.terrain

object Mortgage :

  /**
   * A mortgage strategy to compute the mortgage for a terrain
   */
  trait MortgageStrategy:
    /**
     *
     * @return the money that the owner gain if the mortgage the property
     */
    def computeMortgage: Int

  /**
   * A simple mortgage strategy that divides the buying price by a factor
   * @param price the buying price
   * @param factor the factor
   */
  case class DividePriceMortgage(price: Int, factor: Int) extends MortgageStrategy:
    override def computeMortgage:Int = price/factor
