package terrain.card

import behaviour.BehaviourModule.Behaviour
import behaviour.event.EventGroup

trait Card:
  def consequences: EventGroup
  def name: String

object Card:
  def apply(consequences: EventGroup, name: String): Card =
    DefaultCards(consequences, name)

case class DefaultCards(
    override val consequences: EventGroup,
    override val name: String
) extends Card
