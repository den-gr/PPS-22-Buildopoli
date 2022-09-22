package lib.gameManagement.gameSession

import lib.gameManagement.diceGenerator.{Dice, SingleDice}
import lib.gameManagement.gameBank.{Bank, GameBankImpl}
import lib.gameManagement.gameOptions.GameOptions
import lib.gameManagement.gameStore.GameStore
import lib.gameManagement.gameTurn.{GameJail, GameTurn}
import lib.gameManagement.log.GameLogger
import lib.lap.Lap
import lib.player.{Player, PlayerImpl}
import lib.terrain.{Buildable, GroupManager, Purchasable, PurchasableState, Terrain}
import org.slf4j.{Logger, LoggerFactory}

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
  override val dice: Dice = SingleDice(gameOptions.diceFaces, this.logger)
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
