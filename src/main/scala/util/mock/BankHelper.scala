package util.mock

object BankHelper extends App:
  trait BankMock:
    def money: Int

    def money_=(q: Int): Unit

    def decrement(qty: Int): Unit

  object BankMock:
    val BANK_MONEY = 100
    val TAX = 10

    def apply(): BankMock = new BankMock:
      private var moneyInBank: Int = BANK_MONEY

      override def money: Int = moneyInBank

      override def money_=(q: Int): Unit = moneyInBank = q

      override def decrement(qty: Int): Unit = money -= qty
