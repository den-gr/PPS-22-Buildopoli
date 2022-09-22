package lib.util.mock

object BankHelper extends App:
  enum BankAccount:
    case Bank
    case Player(id: Int)

  trait BankMock:
    var money: Int
    def decrement(qty: Int): Unit
    def getPaymentRequestAmount(from: BankAccount, to: BankAccount): Option[Int]
    def createPaymentRequestAmount(from: BankAccount, to: BankAccount, amount: Int): Unit
    def acceptPayment(from: BankAccount, to: BankAccount): Unit

  object BankMock:
    val BANK_MONEY = 1000
    val TAX = 10

    def apply(): BankMock = new BankMock:
      import BankAccount.*
      private var moneyInBank: Int = BANK_MONEY
      private var paymentRequests = List[(BankAccount, BankAccount, Int)]()

      def money: Int = moneyInBank

      def money_=(q: Int): Unit = moneyInBank = q

      override def decrement(qty: Int): Unit = money -= qty

      override def getPaymentRequestAmount(from: BankAccount, to: BankAccount): Option[Int] =
        paymentRequests.find(t => t._1 == from && t._2 == to).map(_._3)

      override def createPaymentRequestAmount(from: BankAccount, to: BankAccount, amount: Int): Unit =
        if paymentRequests.exists(t => t._1 == from && t._2 == to) then
          throw new IllegalStateException("can not have same payment request")
        else paymentRequests = (from, to, amount) :: paymentRequests

      def acceptPayment(from: BankAccount, to: BankAccount): Unit =
        paymentRequests.find(t => t._1 == from && t._2 == to).get match
          case (_, Bank, am) =>
            decrement(am)
            paymentRequests = paymentRequests.filter(_ != (from, to, am))
          case (_, t: BankAccount, am) if t == Bank => // TODO make payment
            paymentRequests = paymentRequests.filter(_ != (from, to, am))
          case _ => throw IllegalStateException()
