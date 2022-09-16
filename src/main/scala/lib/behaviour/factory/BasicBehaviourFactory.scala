package lib.behaviour.factory

import lib.behaviour.BehaviourModule.Behaviour
import lib.behaviour.event.EventModule.Event
import lib.behaviour.BehaviourModule.Behaviour
import lib.behaviour.event.EventStoryModule.EventStory
import lib.behaviour.factory.input.{JailBehaviourDefaultInput, JailBehaviourInput}

/** Allows easily to create basic behaviours
  */
trait BasicBehaviourFactory:

  /** @param input
    *   a box that contains all inputs that are necessary to create s jail behaviour. By default it uses the values of
    *   [[JailBehaviourDefaultInput]] object
    * @return
    *   jail behaviour that imprison a player for [[input#blockingTime]] turns. Include also the possibility to escape
    *   from prison
    */
  def JailBehaviour(input: JailBehaviourInput = JailBehaviourDefaultInput()): Behaviour
  
  def PurchasableTerrainBehaviour(payRentStory: EventStory, notMoneyErrMsg: String, buyTerrainStory: EventStory): Behaviour
