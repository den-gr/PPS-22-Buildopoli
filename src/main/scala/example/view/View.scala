package example.view

import lib.behaviour.BehaviourModule.StoryGroup
import lib.behaviour.event.EventStoryModule.EventStory
import lib.terrain.Terrain

import scala.annotation.tailrec
import scala.io.StdIn.*

enum PlayerChoice:
  case EndTurn
  case Choice(groupIdx: Int, eventIdx: Int, choiceIdx: Int)

trait View:

  // in base alla scelta si mostra il resto (sempre end turn, dopo il lancio del dado o forse è già gestito dal controller)

  def showCurrentPlayer(playerID: Int): Unit
  def showCurrentTerrain(terrain: Terrain): Unit
  def showStoryOptions(story: Seq[StoryGroup]): Unit
  def printLog(log: String): Unit // log
  def getUserChoices(story: Seq[StoryGroup]): PlayerChoice

case class GameView() extends View:

  def showCurrentPlayer(playerID: Int): Unit = println("* It is player" + playerID + "turn *")
  def showCurrentTerrain(terrain: Terrain): Unit = println("* We are currently in " + terrain.basicInfo.name + '*')

  def showStoryOptions(stories: Seq[StoryGroup]): Unit =
    var i: Int = 0
    var result: String = ""
    stories.foreach(storyGroup =>
      result += "Option " + i + "\n";
      i += 1;
      storyGroup.foreach(story =>
        result += s"\t ${story.description}. Available actions:\n\t\t";
        result += story.choices.mkString("\n\t\t")
        result +=
          "\n"
      )
    )
    println(result)

  def printLog(log: String): Unit = println(s"The log message says ----> $log")

  @tailrec
  private def getChoice(string: String, num: Int): Int =
    println(string)
    val v = readLine()
    if v.toInt < num then v.toInt
    else
      println("Wrong choice, try again!")
      getChoice(string, num)

  def getUserChoices(stories: Seq[StoryGroup]): PlayerChoice =
    val t: PlayerChoice = PlayerChoice.EndTurn
    val gc =
      stories.size match
        case 0 => -1
        case s => getChoice("Choose group event", s)

    if gc == -1 then t
    else
      val ec = getChoice("Choose event", stories(gc).size)

      val eec = stories(gc)(ec).choices.size match
        case 1 => println(stories(gc)(ec).choices.head); 1
        case s => getChoice("Select event choice", s)

      PlayerChoice.Choice(gc, ec, eec)
