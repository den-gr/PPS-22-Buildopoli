package example.controller

import example.view.{PlayerChoice, View}
import lib.behaviour.BehaviourModule.Behaviour
import lib.behaviour.event.{EventFactory, EventGroup}
import lib.behaviour.{BehaviourExplorer, StoryConverter}
import lib.gameManagement.gameSession.GameSession
import lib.gameManagement.gameTurn.GameJail
import lib.gameManagement.log.Observer
import lib.behaviour.event.story.InteractiveEventStoryModule.InteractiveEventStory
import lib.behaviour.event.story.InteractiveEventStoryModule.Result.*

trait GameController:
  def start(): Unit

class GameControllerImpl(gameSession: GameSession, view: View) extends GameController:
  override def start(): Unit =
    gameSession.startGame()
    val observer: Observer = (msg: String) => view.printLog(msg)
    gameSession.logger.registerObserver(observer)

    // todo endgame control
    while true do
      val playerId = gameSession.gameTurn.selectNextPlayer()
      view.showCurrentPlayer(playerId)
      gameSession.movePlayer(playerId)

      val terrain = gameSession.getPlayerTerrain(playerId)
      view.showCurrentTerrain(terrain, gameSession.getPlayerPosition(playerId))

      val behaviourExplorer = Behaviour.combineExplorers(terrain.behaviour, globalBehaviour, playerId)

      while behaviourExplorer.hasNext do
        val stories = behaviourExplorer.currentStories
        view.showStoryOptions(stories)
        view.getUserChoices(stories) match
          case PlayerChoice.Choice(groupIdx, eventIdx, choiceIdx)
              if stories(groupIdx)(eventIdx).isInstanceOf[InteractiveEventStory] =>
            stories(groupIdx)(eventIdx).asInstanceOf[InteractiveEventStory].interactions(choiceIdx)(playerId) match
              case OK => behaviourExplorer.next((groupIdx, eventIdx))
              case ERR(msg) => view.printLog(msg)
          case PlayerChoice.Choice(groupIdx, eventIdx, _) => behaviourExplorer.next((groupIdx, eventIdx))
          case PlayerChoice.EndTurn if behaviourExplorer.canEndExploring => behaviourExplorer.endExploring()
          case PlayerChoice.EndTurn =>
            view.printLog(s"Player $playerId can not end turn because have to explore mandatory events")

  val globalBehaviour = Behaviour(
    EventGroup(
      EventFactory(gameSession).BuildTokenEvent(
        "Terrain where you can build",
        "Select type of building",
        "Select number of building",
        "Not enough money for"
      )
    )
  )
