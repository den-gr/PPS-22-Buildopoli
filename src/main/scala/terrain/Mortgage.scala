package terrain

object Mortgage :

  trait MortgageStrategy:
    def computeMortgage: Int

  case class DividePriceMortgage(price: Int, factor: Int) extends MortgageStrategy:
    override def computeMortgage:Int = price/factor
