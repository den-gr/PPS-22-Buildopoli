package behaviour.factory

import behaviour.BehaviourModule.Behaviour
import behaviour.factory.input.JailBehaviourInput

trait StandardBehaviourFactory:
  private class JailBehaviourInputImpl extends JailBehaviourInput

  def JailBehaviour(input: JailBehaviourInput = new JailBehaviourInputImpl): Behaviour
