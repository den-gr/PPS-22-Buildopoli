package behaviour.factory

import behaviour.BehaviourModule.Behaviour
import sun.security.provider.NativePRNG.Blocking

trait StandardBehaviourFactory:
  def JailBehaviour(blockingTime: Int): Behaviour
