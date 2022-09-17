package example.view

import lib.behaviour.event.story.EventStoryModule.*
import lib.terrain.Terrain

import scala.annotation.tailrec
import scala.io.StdIn.*

enum PlayerChoice:
  case EndTurn
  case Choice(groupIdx: Int, eventIdx: Int, choiceIdx: Int)

trait View:

  def showCurrentPlayer(playerID: Int): Unit
  def showCurrentTerrain(terrain: Terrain, position: Int): Unit
  def showStoryOptions(story: Seq[StoryGroup]): Unit
  def printLog(log: String): Unit
  def getUserChoices(story: Seq[StoryGroup]): PlayerChoice

case class GameView() extends View:

  def END_TURN: Int = -1
  def showCurrentPlayer(playerID: Int): Unit = println(s"* It is player $playerID turn *")
  def showCurrentTerrain(terrain: Terrain, position: Int): Unit = println(
    s"* We are currently in ${terrain.basicInfo.name} at position number $position *"
  )

  def showStoryOptions(stories: Seq[StoryGroup]): Unit =
    var result: String = ""
    stories.zipWithIndex.foreach((storyGroup, i) =>
      result += s"Group Choice $i \n";
      storyGroup.zipWithIndex.foreach((story, i) =>
        result += s"\t Event Choice $i: ${story.description} \n\t\t Available actions:\n";
        story.choices.zipWithIndex.foreach((e, i) => result += s"\t\t\t Action Choice $i: $e")
        result +=
          "\n"
      )
    )
    println(result)

  def printLog(log: String): Unit = println(s"The log message says ----> $log")
  def checkInput(s: String): Option[Int] =
    try Some(s.toInt)
    catch case e: Exception => None

  @tailrec
  private def getChoice(string: String, num: Int): Int =
    println(string)
    checkInput(readLine()) match
      case Some(v) if v < num && v >= END_TURN => v
      case _ => println("Wrong choice, try again!"); getChoice(string, num)

  def getUserChoices(stories: Seq[StoryGroup]): PlayerChoice =
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
