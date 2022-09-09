package terrain.card

import behaviour.BehaviourModule.Behaviour
import behaviour.event.EventGroup

/** Representing the objects inside each CardTerrain Each card has some consequences and a name, identifying it
  */
trait Card:

  /** @return
    *   consequences of a specific cart
    */
  def consequences: EventGroup

  /** @return
    *   name of a specific cart
    */
  def name: String

object Card:
  def apply(consequences: EventGroup, name: String): Card =
    DefaultCards(consequences, name)

case class DefaultCards(
    override val consequences: EventGroup,
    override val name: String
) extends Card
