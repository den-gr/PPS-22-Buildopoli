package buildopoli.gameManagement.gameSession

import buildopoli.gameManagement.diceGenerator.{Dice, SingleDice}
import buildopoli.gameManagement.gameBank.{Bank, GameBankImpl}
import buildopoli.gameManagement.gameOptions.GameOptions
import buildopoli.gameManagement.gameStore.GameStore
import buildopoli.gameManagement.gameTurn.{GameJail, GameTurn}
import buildopoli.gameManagement.log.GameLogger
import buildopoli.lap.Lap
import buildopoli.player.{Player, PlayerImpl}
import buildopoli.terrain.{Buildable, GroupManager, Purchasable, PurchasableState, Terrain}

import scala.collection.mutable.ListBuffer
import scala.util.Random

case class GameSessionImpl(
    override val gameOptions: GameOptions,
    override val gameBank: Bank,
    override val gameTurn: GameTurn,
    override val gameStore: GameStore,
    override val gameLap: Lap
) extends GameSession:

  override val logger: GameLogger = GameLogger()
  override val dice: Dice = Dice(gameOptions.diceFaces, this.logger)
  private var groupManager: GroupManager = _

  override def getGroupManager: GroupManager = this.groupManager

  override def startGame(): Unit =
    this.addPlayers(gameOptions.nUsers)
    this.gameStore.startGame()
    this.initializePlayers()
    this.groupManager = GroupManager(this.gameStore.terrainList)

  private def addPlayers(n: Int): Unit =
    for _ <- 0 until n do this.gameStore.addPlayer()

  private def initializePlayers(): Unit = this.enoughPurchasableTerrains() match
    case true =>
      this.gameStore.playersList.foreach(pl =>
        pl.setPlayerMoney(gameOptions.playerInitialMoney)
        this.assignTerrains(pl.playerId)
      )
    case _ => throw new IllegalStateException("Not enough terrains !")

  private def assignTerrains(playerId: Int): Unit =
    for _ <- 0 until this.gameOptions.playerInitialCells do
      this.getRandomPurchasableTerrainWithoutOwner
        .asInstanceOf[Purchasable]
        .changeOwner(Some(playerId))

  private def getRandomPurchasableTerrainWithoutOwner: Terrain =
    val purchasableTerrainList: Seq[Terrain] =
      this.gameStore.getTypeOfTerrains(tr =>
        tr.isInstanceOf[Purchasable]
          && tr.asInstanceOf[Purchasable].owner.isEmpty
      )
    purchasableTerrainList(Random.nextInt(purchasableTerrainList.size))

  def enoughPurchasableTerrains(): Boolean =
    this.gameStore.getNumberOfTerrains(tr =>
      tr.isInstanceOf[Purchasable]
    ) >= this.gameOptions.nUsers * this.gameOptions.playerInitialCells

  private def setPlayerPosition(isValidLap: Boolean, playerPawnPosition: Int, steps: Int): (Int, Boolean) =
    gameLap.isNewLap(isValidLap, playerPawnPosition, steps, gameStore.getNumberOfTerrains(_ => true))

  override def movePlayer(playerId: Int, isValidLap: Boolean = true, steps: Int = 0): Unit =
    val player: Player = gameStore.getPlayer(playerId)
    if gameTurn.asInstanceOf[GameJail].isPlayerBlocked(playerId) then
      val result: (Int, Boolean) = steps match
        case 0 => setPlayerPosition(isValidLap, player.getPlayerPawnPosition, launchDice)
        case _ => setPlayerPosition(isValidLap, player.getPlayerPawnPosition, steps)
      player.setPlayerPawnPosition(result._1)
      if result._2 then gameLap.giveReward(playerId)

  private def launchDice: Int = this.dice.rollOneDice()
