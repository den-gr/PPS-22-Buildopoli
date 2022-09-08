package behaviour.event

/** Exception that indicate that input defined in [[gameManagement.gameStore.gameInputs.GameInputs]] not correspond to
  * an [[Interaction]] or an [[Event]] expectation
  * @param msg
  *   exception message
  */
class EventInputException(msg: String = "The input in game session not correspond to expected input by Event")
    extends Exception(msg)
