package helper

object TestMocks extends App:
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

  trait JailMock:
    def howManyTurnsPlayerIsBlocked(playerId: Int): Int
    def blockPlayer(playerId: Int, turns: Int): Unit
    def liberatePlayer(playerId: Int): Unit
    def doTurn(): Unit

  object JailMock:
    def apply(): JailMock = new JailMock:
      var blockingList: Map[Int, Int] = Map()
      override def howManyTurnsPlayerIsBlocked(playerId: Int): Int =
        blockingList.getOrElse(playerId, 0)

      override def blockPlayer(playerId: Int, turns: Int): Unit =
        blockingList = blockingList + (playerId -> turns)

      override def liberatePlayer(playerId: Int): Unit =
        blockingList = blockingList + (playerId -> 0)

      override def doTurn(): Unit =
        if !blockingList.values.forall(_ == 0) then
          blockingList.toList.foreach(_ match
            case (key: Int, value: Int) if value > 0 => blockingList = blockingList + (key -> (value - 1))
            case _ =>
          )
