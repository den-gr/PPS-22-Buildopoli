package lib.behaviour.event

import lib.gameManagement.gameStore.gameInputs.GameInputs

/** Indicate that input defined in [[GameInputs]] not correspond to an [[Interaction]] or an [[Event]] expectation
  *
  * @param message
  *   exception message
  */
class EventInputException(message: String = "The input in game session not correspond to expected input by Event")
    extends Exception(message)
