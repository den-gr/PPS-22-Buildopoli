package util.mock

object JailHelper:
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
