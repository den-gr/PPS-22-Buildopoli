package lib.behaviour

import BehaviourModule.*
import lib.behaviour.event.EventStoryModule.EventStory
import lib.behaviour.event.{EventGroup, GenericBehaviourExplorer}
import scala.collection.mutable

/** Allows correctly navigate between event groups and their successors of a Behaviour. Encapsulate a sequence of
  * [[EventGroup]]
  */
trait BehaviourExplorer extends GenericBehaviourExplorer[Seq[EventGroup]] with StoryConverter:
  /** Index of an event of the behaviour. It is a tuple2: (eventGroupIndex, eventIndex)
    */
  override type Index = (Int, Int)

  /** Choice of next event
    * @param index
    *   is a double tuple where first element is event group index, and second is an event index. Default value (0, 0)
    */
  override def next(index: (Int, Int) = (0, 0)): Unit

/** Allows extract stories from event groups
  */
trait StoryConverter:
  ex: GenericBehaviourExplorer[Seq[EventGroup]] =>

  /** Get stories of current event groups
    *
    * @return
    *   grouped event stories
    */
  def currentStories: Seq[StoryGroup] =
    ex.currentEvents.map(eventGroup => eventGroup.map(m => m.eventStory(ex.playerId)))

object BehaviourExplorer:

  /** Constructor of behaviour explorer
    * @param events
    *   the event groups that must be explored
    * @param playerId
    *   id of player that interact with behaviour
    * @return
    *   behaviour explorer that allows to a player to use game events
    */
  def apply(events: Seq[EventGroup], playerId: Int): BehaviourExplorer with StoryConverter =
    BehaviourExplorerImpl(events, playerId)

  /** Add a new event group to the current event groups of behaviour explorer. Can be useful if behaviour can has random
    * events. It is better to use this constructor if behaviour explorer was not used
    *
    * @param explorer
    *   behaviour explorer that will receive a new event group
    * @param eventGroup
    *   new event group that will be appended to explorer
    * @return
    *   behaviour explorer with new event group
    */
  def apply(
      explorer: BehaviourExplorer,
      eventGroup: EventGroup
  ): BehaviourExplorer with StoryConverter =
    apply(eventGroup +: explorer.currentEvents, explorer.playerId)

  private case class BehaviourExplorerImpl(events: Seq[EventGroup], override val playerId: Int)
      extends BehaviourExplorer
      with StoryConverter:

    val eventStack: mutable.Stack[Seq[EventGroup]] = mutable.Stack(events)

    override def hasNext: Boolean = eventStack.nonEmpty && eventStack.head.nonEmpty

    override def next(index: Index): Unit =
      val groups = eventStack.pop()
      index match
        case (groupIndex: Int, eventIndex: Int)
            if groupIndex < 0 || groupIndex >= groups.length || eventIndex >= groups(groupIndex).length =>
          eventStack.push(groups) // redo pop
          throw IllegalArgumentException(s"Chose index point to a not existing event. -> $index")

        case (groupIndex: Int, eventIndex: Int) =>
          val newGroup = chooseEvent(groups(groupIndex))(playerId, eventIndex)
          if groups(groupIndex).isAtomic then
            eventStack.push(groups.patch(groupIndex, Nil, 1))
            if newGroup.nonEmpty then eventStack.push(Seq(newGroup.get))
          else if newGroup.nonEmpty then eventStack.push(newGroup.get +: groups.patch(groupIndex, Nil, 1))

    override def currentEvents: Seq[EventGroup] = eventStack.head

    override def canEndExploring: Boolean = eventStack.size <= 1 && !eventStack.head.exists(_.isMandatory)

    override def endExploring(): Unit = eventStack.clear()

    private def chooseEvent(eventGroup: EventGroup)(playerId: Int, index: Int): Option[EventGroup] =
      try
        val event = eventGroup(index)
        event.run(playerId)
        if event.nextEvent.nonEmpty then
          val next = event.nextEvent.get
          Some(next.replaceEvents(next.filter(_.hasToRun(playerId))))
        else None
      catch
        case _: IndexOutOfBoundsException =>
          throw IllegalArgumentException("Chose index of a not existing event. -> " + index)
