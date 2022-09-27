package example.view

import lib.behaviour.event.story.EventStoryModule.*
import lib.terrain.Terrain

import scala.annotation.tailrec
import scala.io.StdIn.*

/** PlayerChoice represents the choice of the player that can decide to end his turn or to keep going until three single
  * choices are made
  */
enum PlayerChoice:
  /** It is used when the player decides to end his turn
    */
  case EndTurn

  /** It is used when the player decides the event group id, the event id and the single action id
    */
  case Choice(groupIdx: Int, eventIdx: Int, choiceIdx: Int)

trait View:
  /** It determines how the player is displayed
    * @param playerID
    *   is the id of the player to be displayed
    * @param money
    *   is the current amount of money the player has
    */
  def showCurrentPlayer(playerID: Int, money: Int): Unit

  /** It determines how the terrain is displayed
    * @param terrain
    *   is the cell to be shown
    * @param position
    *   is the position of the cell to be shown
    */
  def showCurrentTerrain(terrain: Terrain, position: Int): Unit

  /** It determines how the available choices are displayed
    * @param story
    *   contain the available choices
    */
  def showStoryOptions(story: Seq[StoryGroup]): Unit

  /** It determines how an error should be displayed
    * @param msgError
    *   the explanation of the erro
    */
  def showError(msgError: String): Unit

  /** It is used to show what the Log object already shows
    * @param log
    *   is the Log output
    */
  def printLog(log: String): Unit

  /** It is used to wait in a blocking way for the player's input
    * @param story
    *   represents the available options
    * @return
    *   the complete choice
    */
  def getUserChoices(story: Seq[StoryGroup]): PlayerChoice

case class GameView() extends View:

  def END_TURN: Int = -1

  override def showCurrentPlayer(playerID: Int, playerMoney: Int): Unit = println(
    s"\n\n* It is player $playerID turn * \n* Available money: $playerMoney *"
  )

  override def showCurrentTerrain(terrain: Terrain, position: Int): Unit = println(
    s"* We are currently in >>>${terrain.basicInfo.name}<<< at position number $position *"
  )

  override def showStoryOptions(stories: Seq[StoryGroup]): Unit =
    var result: String = ""
    stories.zipWithIndex.foreach((storyGroup, i) =>
      result += s"Group Choice $i \n";
      storyGroup.zipWithIndex.foreach((story, i) =>
        result += s"\t Event Choice $i: ${story.description} \n\t\t Available actions:\n";
        story.choices.zipWithIndex.foreach((e, i) => result += s"\t\t\t Action Choice $i: $e\n")
      )
    )
    println(result)

  override def showError(msgError: String): Unit = println(s"!!! ERROR !!! $msgError")

  override def printLog(log: String): Unit = println(s"The log message says ----> $log")
  def checkInput(s: String): Option[Int] =
    try Some(s.toInt)
    catch case e: Exception => None

  override def getUserChoices(stories: Seq[StoryGroup]): PlayerChoice =
    val gc =
      stories.size match
        case s => getChoice(s"Choose group event or digit $END_TURN to end the turn", s)

    if gc == END_TURN then PlayerChoice.EndTurn
    else
      val ec = forceSingleChoice(stories(gc).size, stories(gc).head.description)
      val eec = forceSingleChoice(stories(gc)(ec).choices.size, stories(gc)(ec).choices.head)
      PlayerChoice.Choice(gc, ec, eec)

  private def forceSingleChoice(size: Int, description: String): Int = size match
    case 1 => println(description); 0
    case s => getChoice("Choose event", s)

  @tailrec
  private def getChoice(string: String, num: Int): Int =
    println(string)
    checkInput(readLine()) match
      case Some(v) if v < num && v >= END_TURN => v
      case _ => println("Wrong choice, try again!"); getChoice(string, num)
