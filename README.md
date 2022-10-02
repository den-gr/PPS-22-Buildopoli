# Buildopoli

**Buildopoli** is a library for developing Monopoli based games.

## Features

The library is written in **scala** and provides the user with an environment in which functional programming is blended with object oriented design to achieve flexibility and extensibility.

Buildopoli offers the following main features and abstractions:

* A **set of terrains** where the **player** can place **pawns** on **terrains** via moves after launching **dice**;

* An easy deployable basic game configuration, starting from **Game Session**. Configurable via **Game Options**;

* Extensions to handle **players**, **turns**, **terrains**,**ending conditions**, **events** and **behaviours**;

* Different and configurable **Terrains** like purchasable, buildable or card terrains;

* An interacting simple example with a CLI view already implemented that enables the user to display the status of the game he just created and with whom is possible to interact among events that occurs on different turns and terrains.

## Requirements

To use the library you need the following software installed:

* Scala version 3.2.0
* Sbt version 1.7.1

An IDE is strongly recommended.

## How to use

### Download

Get the latest Jar from the GitHub releases page.

### Import

Open a project in IntelliJ IDEA, then from the the main menu, select **File | Project Structure | Project Settings | Modules**.

Select the module for which you want to add a library and click on the **Dependencies** tab.

Click the **Add** button and select **JARs or directories...**.

Provide the downloaded Jar.

If you what the documentation and source code you can right click on the library, select **Edit... | Add**.

Select from GitHub releases page the sources Jar.

In the **Choose Category and ...** pop-up menu, select **Sources** and press **OK** button.

### Code

Import buildopoli and you're ready to code!

## Examples

In Buildopoli you can play the game in it's most basic form with some very simple steps.

### Starting with initializing Game session

From GameSession you are able to control the whole game while playing it.
You can setup: turns, players, terrains, tha bank, the lap.

To accomplish all this semplicity in settting up the game, some dependencies between classes were requested.
For example: game bank (who manages the bank into Monopoli games), needs some knowledge about player's inside the game.
But that list is stored inside GameStore (who acts as a repository for some basic data structures of the game, like player's
or terrain's list)

So, to start implementing the game, you need to initialize GameStore:
```scala
    val gameStore: GameStore = GameStore()
```
like that. As simple as it can be.

Now you can proceed, creating some other basic components of the game:
```scala
    val gameBank: Bank = GameBankImpl(gameStore)
```
Please note that gameBank needs knowledge about gameStore.

It's time to select some strategies to be used into the game:
```scala
    val endGame = playerId => EndGame.defeatedForNoMoneyAndNoTerrainsOwned(playerId, gameStore, gameBank)
```
Let's explain it: endGame is used to specify and manage the ending of the game (and so, removing players during the game, that lose)
The basic implementations is explained up: using a static method of EndGame object. In this case, players lose when they have no money
and no owned terrains. Obviously, when only one player remains, he is the winner.

Let's proceed with specifying the selector:
```scala
  private val selector: (Seq[Player], Seq[Int]) => Int =
  (playerList: Seq[Player], playerWithTurn: Seq[Int]) =>
    playerList.find(el => !playerWithTurn.contains(el.playerId)).head.playerId
```
Let's explain it: selector is used by GameTurn (who manages turns into the game itself), to select the next player who has to play the game.
The most simple and basic implementation is like that proposed.

The first list contains all the players into the game. The last one contains players who have already done the actual turn.
Now we take the head of the elements of the first list that not appears into the last given list.

Now, let's put somewhere all those strategies. Here it comes GameOptions:
```scala
    val gameOptions: GameOptions =
      GameOptions(playerInitialMoney, playerInitialCells, numberOfPlayers, diceFaces, selector, endGame)
```
GameOptions is extremely useful: it gives the possibility to specify lot of parameters to personalize the game.
In order, you can specify:
* initial money to give to any player at start of the game
* initial cells, like previous element
* number of players into the game
* number of faces in the dice. Dice it's used in the game to move each player position.
* selector, specified and explained before
* endGame strategy, specified and explained before

Only two last things remains: GameTurn and GameLap:
```scala
    val gameTurn: GameTurn = GameTurn(gameOptions, gameStore)
    val gameLap: Lap = Lap(MoneyReward(gameLapMoneyReward, gameBank))
```
Like gameBank before, gameTurn and gameLap need some knowledge about other elements previously created.
let's explain them:
* GameTurn manages turns into the game. It needs gameOptions and gameStore, in the most basic implementation given with the library.
  * Every time you need to pass to the next turn it does three things: check if there are inputs to consume by the previous player,
  checks if some player have to be removed because have lose the game and then (using selector) selects the next player that has to play.
* GameLap, manages position of each user in each cell during the game. Needs one major element inside:
  * The type of reward to give at any player when completes one lap into the game. The provided reward, gives some specified money
  to each user that completes one lap (You can easily implements one by yourself).

Now all major components of the game are instanced and initialized. Let's finally create Game Session:
```scala
    val gs = GameSessionImpl(gameOptions, gameBank, gameTurn, gameStore, gameLap)
```

To summarise all the above concepts, here it is one global snapshot for **GameSession Initializer**:
```scala
object GameSessionInitializer:
  private val selector: (Seq[Player], Seq[Int]) => Int =
    (playerList: Seq[Player], playerWithTurn: Seq[Int]) =>
      playerList.find(el => !playerWithTurn.contains(el.playerId)).head.playerId
  private val playerInitialMoney = 200
  private val playerInitialCells = 0
  private val diceFaces = 3
  private val gameLapMoneyReward = 100

  def createDefaultGameSession(numberOfPlayers: Int): GameSession =
    val gameStore: GameStore = GameStore()
    val gameBank: Bank = GameBankImpl(gameStore)
    val endGame = playerId => EndGame.defeatedForNoMoneyAndNoTerrainsOwned(playerId, gameStore, gameBank)
    val gameOptions: GameOptions =
      GameOptions(playerInitialMoney, playerInitialCells, numberOfPlayers, diceFaces, selector, endGame)
    val gameTurn: GameTurn = GameTurn(gameOptions, gameStore)
    val gameLap: Lap = Lap(MoneyReward(gameLapMoneyReward, gameBank))

    val gs = GameSessionImpl(gameOptions, gameBank, gameTurn, gameStore, gameLap)
    gs
```
That's it. Let's continue with the next components to initialize Monopoli !

### Let's create some Terrains

Now you should decide which terrains will be part of your game!

Remember that Buildopoli allows you to easily create the fundamental terrains types of classic Monopoli to design the Monopoli game of your dreams!

Now I’ll show you how to replicate some of the popular Monopoli’s terrains with our library.

**Terrain** is meant to make simple terrains such as the starting point, all you need to do is choosing a name and specify an empty behaviour:
```scala
val t0: Terrain = Terrain(TerrainInfo("Go"), Behaviour())
```

It is also possible to create the “income tax” cell that drains the player's money. 
You can easily create a WithdrawMoneyEvent provided by the library.
```scala
val story = EventStory(s"You spend $amount money on a party", "Oh, noo")
val behaviour = Behaviour(eventFactory.WithdrawMoneyEvent(story, amount))
```
combine it and the terrain is ready!
```scala
val t1: Terrain = Terrain(TerrainInfo("Party"), behaviour)
```

With **Purchasable** it is easy to create terrains that players can buy but where it is NOT possible to build. 
Let’s create our train station. 
First we create the encapsulated terrain with the name and a PurchasableTerrainBehaviour.

```scala
val buyStory = EventStory(s"You have an incredible opportunity to buy $stationName", "Buy station")
val rentStory = EventStory(s"You are at $stationName and must pay for the ticket", "Pay for ticket")
val errMsg = s"You have not enough money to buy $stationName"
val behaviour = behaviourFactory.PurchasableTerrainBehaviour(rentStory, errMsg, buyStory)
var t2: Terrain = Terrain(TerrainInfo("Train Station"), behaviour)
```
Then decide the selling price, the group and the strategies to compute the Mortgage and the Rent from the ones provided by the library. 
```scala
t2 = Purchasable(
        t2,
        300,
        "stations",
        DividePriceMortgage(price, 2),
        RentStrategyPreviousPriceMultiplier(50, 2)
      )
```
Now you can try adding another station to the game to form a group!

It is useful to use **Buildable** to create terrains such as the Monopoli’s groups of places where it is possible to build houses and hotels.
First we need to make the Token: 
it is possible to specify that we want two levels (house and hotels) and the maximum number for each level and their prices
```scala
val token = Token(Seq("house", "hotel"), Seq(Seq(50, 50), Seq(100)), Seq(25, 50))
```
We then combine the encapsulated Purchasable with the token.

```scala
val buyStory = EventStory(s"You can buy terrain on $streetName", "Buy terrain")
val rentStory = EventStory(s"You ara at $streetName, you must puy rent to the owner", "Pay rent")
val errMsg = "You have not enough money to pay for the rent"
val behaviour = behaviourFactory.PurchasableTerrainBehaviour(rentStory, errMsg, buyStory)
val purchasableTerrain = Purchasable(
    Terrain(TerrainInfo("Bologna"), behaviour),
    100,
    "Italy",
    DividePriceMortgage(price, 2),
    buildopoli.terrain.RentStrategy.BasicRentStrategyFactor(100, 2)
    )
      
val t3: Terrain = Buildable(purchasableTerrain, token)
```

With Buidopoli you can also add the famous **Probabilities** and **Surprises** that make the players draw a card. 
Some cards make the player gains money while others give the player an extra lap.
To add porbabilities to the game all you need is the encapsulated terrain with an empty behaviour, the gameSession and false.

```scala
var t4: Terrain = Terrain(TerrainInfo("Probabilities"), Behaviour())
CardTerrain(t4, gameSession, false)

```
Once created the desidered terrains, they must be added to the game. We create:
```scala
var terrains: Seq[Terrain] = Seq()
```

to store them in the order you want them to be in the game. And now it is time to add them:
```scala
terrains = terrains :+ t0
terrains = terrains :+ t1
terrains = terrains :+ t2
terrains = terrains :+ t3
terrains = terrains :+ t4
```

### What about terrain's and global Behaviours ?

To have terrains is not enough! We need to implement behaviours for them.
Each behaviour is made by events, so let's create an event that allow to player to buy terrains

At the start we must have a reference to `gameSession` to be able to get and set game state

We start from creating a precondition that define when the event will be visible to player:
```scala
val precondition: EventPrecondition = playerId =>
  gameSession.getPlayerTerrain(playerId) match
    case t: Purchasable if t.state == IN_BANK => true
    case _: Purchasable => false
    case t => throw IllegalStateException(s"BuyTerrainEvent is not compatible with ${t.getClass}")
```
Here we check that our terrain has the correct type and state is `IN_BANK` that means that the terrain has not an owner.
By default Event precondition is always `true` (event is always visible)

When we define the availability condition of our event we need to define a story interaction.
```scala
val story = EventStory("You can buy this beautiful terrain", "Buy it")

val interaction: Interaction = playerId =>
  val playerMoney = gameSession.gameBank.getMoneyOfPlayer(playerId)
  gameSession.getPlayerTerrain(playerId) match
    case t: Purchasable if playerMoney >= t.price => Result.OK
    case _: Purchasable => Result.ERR("You have not enough money to buy this terrain")
```
If the player has not money to buy the terrain we will send him a feedback  inside the `ERR` otherwise we return `OK` that
means that event can be run.

In cases where our `EventStory` return always `OK`  we do not need an `Interaction`

When our interaction is ready we can combine it with our story.
```scala
val interactiveStory = EventStory(story, Seq(interaction))
```
For `EventStory` and other classes there are multiple constructors that can simplify use of the library.

At this point we need to define main strategy of the event
```scala

val strategy: EventStrategy = playerId =>
  gameSession.getPlayerTerrain(playerId) match
    case t: Purchasable if t.state != IN_BANK =>
      throw IllegalStateException("Player can not buy already purchased terrain")
    case t: Purchasable => // OK
      if bank.getMoneyOfPlayer(playerId) >= t.price then
        bank.makeTransaction(playerId, amount = t.price) // get terrain price
        t.changeOwner(Some(playerId)) // change terrain owner
      else
        throw IllegalStateException(
          s"Player $playerId has not enough money =>  ${bank.getMoneyOfPlayer(playerId)} but need ${t.price}"
        )
    case t => throw IllegalStateException(s"BuyTerrainEvent is not compatible with ${t.getClass}")
```
For security we handle a lot of possible exceptions even if they must not happen.

Finally we assemble the Event and put it inside the Behaviour
```scala
val event = Event(interactiveStory, strategy, precondition)
Behaviour(event)
```

This is a typical event of Monopoly so our team make a factory that build it for you, for example:
```scala
val buyStory = EventStory(s"You can buy terrain on $streetName", "Buy terrain")
val rentStory = EventStory(s"You ara at $streetName, you must puy rent to the owner", "Pay rent")
val errMsg = "You have not enough money to pay for the rent"
val behaviour = BehaviourFactory(gameSession).PurchasableTerrainBehaviour(rentStory, errMsg, buyStory)
```
This behaviour include two event: one for buying the terrain (that we saw here) and one for getting the rent from another players

### But let's play this game finally => Game Controller !
We must finish to assemble our game
```scala
val NUMBER_OF_PLAYERS = 2
val gameSession = GameSessionInitializer.createDefaultGameSession(NUMBER_OF_PLAYERS)
val terrains = TerrainInitializer(gameSession).buildGameTerrains()
gameSession.gameStore.terrainList ++= terrains
gameSession.gameStore.globalBehaviour = GlobalBehaviourInitializer(gameSession).buildGlobalBehaviour()

GameControllerImpl(gameSession, GameView()).start()
```
We also added a global behaviour that contains events available to players in all terrains (if event precondition allow it)

So how is made our game controller?
```scala
class GameControllerImpl(gameSession: GameSession, view: View):
  override def start(): Unit =
    //we need to visualize game log to the player
    gameSession.logger.registerObserver((msg: String) => view.printLog(msg))

    gameSession.startGame()
  
    while !gameSession.isGameEnded do
      //select a player
      val playerId = gameSession.gameTurn.selectNextPlayer()

      //launch dices and move the player pawn
      gameSession.movePlayer(playerId)
  
      //preparing to explore available avents
      var behaviourExplorer = gameSession.getFreshBehaviourExplorer(playerId) 
  
      //start exploring
      while behaviourExplorer.hasNext do
        val stories = behaviourExplorer.currentStories
        view.showStoryOptions(stories)
        view.getUserChoices(stories) match
          case PlayerChoice.Choice(groupIdx, eventIdx, choiceIdx) 
            if stories(groupIdx)(eventIdx).isInstanceOf[InteractiveEventStory] =>
              val interactiveStory = stories(groupIdx)(eventIdx).asInstanceOf[InteractiveEventStory]]
              interactiveStory.interactions(choiceIdx)(playerId) match
                case OK => behaviourExplorer = behaviourExplorer.next((groupIdx, eventIdx))
                case ERR(msg) => view.printLog(msg)
          case PlayerChoice.Choice(groupIdx, eventIdx, _) =>
            behaviourExplorer = behaviourExplorer.next((groupIdx, eventIdx))
          case PlayerChoice.EndTurn if behaviourExplorer.canEndExploring =>
            behaviourExplorer = behaviourExplorer.endExploring()
          case PlayerChoice.EndTurn =>
            view.printLog(s"Player $playerId can not end turn because have to explore mandatory events")
```
This is basic game controller structure that can be personalized by you!

And finally we are ready to play!