package behaviour.factory
import behaviour.BehaviourModule.*
import behaviour.event.EventModule.*
import behaviour.event.EventStoryModule.EventStory
import gameManagement.gameTurn.GameTurn

import scala.util
import scala.util.Random

object BehaviourFactory:

  def apply(gameTurn: GameTurn): StandardBehaviourFactory = BehaviourFactoryImpl(gameTurn)

  class BehaviourFactoryImpl(gameTurn: GameTurn) extends StandardBehaviourFactory:
    // TODO blockingTime unused
    override def JailBehaviour(blockingTime: Int): Behaviour =
      val story: EventStory = EventStory(s"You are imprisoned for $blockingTime turns", Seq("Wait liberation"))
      val imprisonStrategy: Int => Unit = playerId =>
        gameTurn.getRemainingBlockedMovements(playerId) match
          case None =>
            gameTurn.lockPlayer(playerId)
          case _ =>
      val imprisonEvent: Event = Event(Scenario(imprisonStrategy, story))
      val escapeStrategy: Int => Unit = playerId =>
        if Random.nextInt(6) == Random.nextInt(6) then
          // TODO logger(User is escaped)
          gameTurn.liberatePlayer(playerId)
        // else
        // TODO logger(fail to escape)
      val escapeStory: EventStory = EventStory(s"You have an opportunity to escape", Seq("Try to escape"))
      val escapePrecondition: EventPrecondition = gameTurn.getRemainingBlockedMovements(_).nonEmpty
      val escapeEvent: Event = Event(Scenario(escapeStrategy, escapeStory), escapePrecondition)
      Behaviour(Seq(EventGroup(imprisonEvent, escapeEvent)))
