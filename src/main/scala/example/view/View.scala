package example.view

import lib.behaviour.BehaviourModule.StoryGroup
import lib.behaviour.event.EventStoryModule.EventStory
import lib.terrain.Terrain

import scala.annotation.tailrec
import scala.io.StdIn.*

trait View:

  // in base alla scelta si mostra il resto (sempre end turn, dopo il lancio del dado o forse è già gestito dal controller)

  def showCurrentPlayer(playerID: Int): Unit
  def showCurrentTerrain(terrain: Terrain): Unit
  def showStoryOptions(story: Seq[StoryGroup]): Unit
  def printLog(log: String): Unit // log
  def getUserChoices(story: Seq[StoryGroup]): (Int, Int, Int)

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

  def printLog(log: String): Unit = println(log)

  @tailrec
  private def getChoice(string: String, num: Int): Int =
    println(string)
    val v = readLine()
    if v.toInt < num then v.toInt
    else
      println("Wrong choice, try again!")
      getChoice(string, num)

  def getUserChoices(stories: Seq[StoryGroup]): (Int, Int, Int) =
    val gc = getChoice("Choose group event", stories.size)
    val ec = getChoice("Choose event", stories(gc).size)
    var eec = 0
    stories(gc)(ec).choices.size match
      case s if s > 1 => eec = getChoice("Select event choice", s)
      case _ => println(stories(gc)(ec).choices.head)
    (gc, ec, eec)
