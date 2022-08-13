package helper

import scala.collection.mutable

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
    def getRemainingBlockedMovements(playerId: Int): Option[Int]
    def blockPlayer(playerId: Int, turns: Int): Unit
    def liberatePlayer(playerId: Int): Unit
    def doTurn(): Unit

  object JailMock:
    def apply(): JailMock = new JailMock:
      var blockingList: Map[Int, Int] = Map()
      override def getRemainingBlockedMovements(playerId: Int): Option[Int] =
        blockingList.get(playerId)

      override def blockPlayer(playerId: Int, turns: Int): Unit =
        blockingList = blockingList + (playerId -> turns)

      override def liberatePlayer(playerId: Int): Unit =
        blockingList = blockingList - playerId

      override def doTurn(): Unit =
        if blockingList.nonEmpty then
          blockingList.toList.foreach(_ match
            case (key: Int, value: Int) if value >= 0 => blockingList = blockingList + (key -> (value - 1))
          )
          blockingList = blockingList.filter(_._2 >= 0)
          
  

