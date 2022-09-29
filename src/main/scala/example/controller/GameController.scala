package example.controller

import example.view.{PlayerChoice, View}
import buildopoli.behaviour.BehaviourModule.Behaviour
import buildopoli.behaviour.event.EventGroup
import buildopoli.behaviour.{BehaviourExplorer, StoryConverter}
import buildopoli.gameManagement.gameSession.GameSession
import buildopoli.gameManagement.gameTurn.GameJail
import buildopoli.gameManagement.log.Observer
import buildopoli.behaviour.event.story.InteractiveEventStoryModule.InteractiveEventStory
import buildopoli.behaviour.event.story.InteractiveEventStoryModule.Result.*
import buildopoli.behaviour.factory.EventFactory

/**
 * Simple controller of the game
 */
trait GameController:
  /**
   * Start the game. Can be called only ones
   * @throws IllegalStateException if it is called more than one time
   */
  def start(): Unit

class GameControllerImpl(gameSession: GameSession, view: View, val maxMoves: Int = Int.MaxValue) extends GameController:
  private var moves = 0
  private var started = false

  override def start(): Unit =
    if started then throw IllegalStateException("Can not start a game twice")
    started = true

    gameSession.logger.registerObserver((msg: String) => view.printLog(msg))

    gameSession.startGame()

    while !gameSession.isGameEnded && this.moves < maxMoves do
      this.moves += 1
      val playerId = gameSession.gameTurn.selectNextPlayer()
      view.showCurrentPlayer(playerId, gameSession.gameBank.getMoneyOfPlayer(playerId))
      gameSession.movePlayer(playerId)

      val terrain = gameSession.getPlayerTerrain(playerId)
      view.showCurrentTerrain(terrain, gameSession.getPlayerPosition(playerId))

      var behaviourExplorer = gameSession.getFreshBehaviourExplorer(playerId)

      while behaviourExplorer.hasNext do
        val stories = behaviourExplorer.currentStories
        view.showStoryOptions(stories)
        view.getUserChoices(stories) match
          case PlayerChoice.Choice(groupIdx, eventIdx, choiceIdx)
              if stories(groupIdx)(eventIdx).isInstanceOf[InteractiveEventStory] =>
            stories(groupIdx)(eventIdx).asInstanceOf[InteractiveEventStory].interactions(choiceIdx)(playerId) match
              case OK => behaviourExplorer = behaviourExplorer.next((groupIdx, eventIdx))
              case ERR(msg) => view.printLog(msg)
          case PlayerChoice.Choice(groupIdx, eventIdx, _) =>
            behaviourExplorer = behaviourExplorer.next((groupIdx, eventIdx))
          case PlayerChoice.EndTurn if behaviourExplorer.canEndExploring =>
            behaviourExplorer = behaviourExplorer.endExploring()
          case PlayerChoice.EndTurn =>
            view.printLog(s"Player $playerId can not end turn because have to explore mandatory events")
    view.printLog(s"End game on $moves move")
