package example.controller

import lib.gameManagement.gameSession.GameSession

trait GameEngine:
  def start(): Unit

class GameEngineImpl(gameSession: GameSession) extends GameEngine:
  override def start(): Unit =
    while true do
      val playerId = gameSession.gameTurn.selectNextPlayer()
      if gameSession.gameTurn.getRemainingBlockedMovements(playerId).isEmpty then
        gameSession.setPlayerPosition(playerId, gameSession.dice.rollOneDice())
      val behaviourIterator = gameSession.getPlayerTerrain(playerId).getBehaviourIterator(playerId)
      while behaviourIterator.hasNext do
        println(behaviourIterator.currentEvents) // todo show to user
        behaviourIterator.next() // todo user make a choice
        // todo if user click endturn then exit from the cycle
      // todo control endgame
