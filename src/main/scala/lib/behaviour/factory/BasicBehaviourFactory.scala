package lib.behaviour.factory

import lib.behaviour.BehaviourModule.Behaviour
import lib.behaviour.event.EventModule.Event
import lib.behaviour.BehaviourModule.Behaviour
import lib.behaviour.event.story.EventStoryModule.EventStory
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

  /** @param payRentStory
    *   event story for the event that request pay rent to the terrain owner 
    * @param notMoneyErrMsg error message that will be displayed when a player has not money to buy a terrain
    * @param buyTerrainStory event story for the event allows to buy a terrain
    * @return behaviour for purchasable terrain that allows buy it and get rent payments
    */
  def PurchasableTerrainBehaviour(
      payRentStory: EventStory,
      notMoneyErrMsg: String,
      buyTerrainStory: EventStory
  ): Behaviour
