package example.controller

import example.view.{PlayerChoice, View}
import lib.behaviour.event.EventStoryModule.InteractiveEventStory
import lib.gameManagement.gameSession.GameSession
import lib.gameManagement.log.Observer

trait GameController:
  def start(): Unit

class GameControllerImpl(gameSession: GameSession, view: View) extends GameController:
  override def start(): Unit =
    gameSession.startGame()
    val observer: Observer = new Observer:
      override def update(msg: String): Unit = view.printLog(msg)
    gameSession.logger.registerObserver(observer)
    while true do
      val playerId = gameSession.gameTurn.selectNextPlayer()
      view.showCurrentPlayer(playerId)
      if gameSession.gameTurn.getRemainingBlockedMovements(playerId).isEmpty then
        gameSession.setPlayerPosition(playerId, gameSession.dice.rollOneDice())
      val terrain = gameSession.getPlayerTerrain(playerId)
      view.showCurrentTerrain(terrain, gameSession.getPlayerPosition(playerId))
      val behaviourIterator = terrain.getBehaviourIterator(playerId)
      while behaviourIterator.hasNext do
        val stories = behaviourIterator.currentStories
        view.showStoryOptions(stories)
        view.getUserChoices(stories) match
          case PlayerChoice.Choice(groupIdx, eventIdx, choiceIdx)
              if stories(groupIdx)(eventIdx).isInstanceOf[InteractiveEventStory] =>
            stories(groupIdx)(eventIdx).asInstanceOf[InteractiveEventStory].choices(choiceIdx)
            behaviourIterator.next((groupIdx, eventIdx))
          case PlayerChoice.Choice(groupIdx, eventIdx, _) => behaviourIterator.next((groupIdx, eventIdx))
          case PlayerChoice.EndTurn =>
        // todo if user click end turn then exit from the cycle
      // todo control endgame
