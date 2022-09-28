package buildopoli.gameManagement.gameStore.gameInputs

import scala.collection.mutable.ListBuffer

trait GameInputs:

  /** @return
    *   a list where to store all input values for a specific player in a specific turn. This list must be empty to
    *   proceed on turns.
    */
  var inputList: Seq[Any]

  /** @param element
    *   to add at Tail of the list
    */
  def addTailInputEvent(element: Any): Unit

  /** Used to remove the element at the head of the list
    */
  def removeHeadElement(): Unit

  /** @return
    *   the element at the head of the list
    */
  def getHeadElement: Any

  /** @return
    *   if the list is empty or not
    */
  def isListEmpty: Boolean = inputList.isEmpty

object GameInputs:
  def apply(): GameInputs = UserInputs()

private case class UserInputs() extends GameInputs:
  var userInput: Seq[Any] = Seq()

  def inputList: Seq[Any] = userInput
  def inputList_=(list: Seq[Any]): Unit = this.userInput = list

  override def addTailInputEvent(element: Any): Unit =
    userInput = userInput :+ element

  override def removeHeadElement(): Unit =
    userInput = userInput.drop(1)

  override def getHeadElement: Any = userInput.head
