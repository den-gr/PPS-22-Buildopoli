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

  def showCurrentPlayer(playerID: Int): Unit
  def showCurrentTerrain(terrain: Terrain): Unit
  def showStoryOptions(story: Seq[StoryGroup]): Unit
  def printLog(log: String): Unit
  def getUserChoices(story: Seq[StoryGroup]): PlayerChoice

case class GameView() extends View:

  def showCurrentPlayer(playerID: Int): Unit = println(s"* It is player $playerID turn *")
  def showCurrentTerrain(terrain: Terrain): Unit = println(s"* We are currently in ${terrain.basicInfo.name} *")

  def showStoryOptions(stories: Seq[StoryGroup]): Unit =
    var ei: Int = 0
    var a: Int = 0
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
