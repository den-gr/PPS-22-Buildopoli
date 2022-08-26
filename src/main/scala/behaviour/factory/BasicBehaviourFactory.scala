package behaviour.factory

import behaviour.BehaviourModule.Behaviour
import behaviour.factory.input.JailBehaviourInput
import behaviour.factory.input.tempate.JailBehaviourInputTemplate

/** Allows easily to create basic behaviours
  */
trait BasicBehaviourFactory:
  /** @param input
    *   a box that contains all inputs that are necessary to create s jail behaviour. By default it uses the values of
    *   [[JailBehaviourInputTemplate]] object
    * @return
    *   jail behaviour that imprison a player for [[input#blockingTime]] turns. Include also the possibility to escape
    *   from prison
    */
  def JailBehaviour(input: JailBehaviourInput = JailBehaviourInputTemplate): Behaviour