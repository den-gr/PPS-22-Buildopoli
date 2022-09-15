package lib.gameManagement.log

trait GameLogger:
  def log(message: String): Unit
  def registerObserver(observer: Observer): Unit
  
trait Observer:
  def update(message: String): Unit

object GameLogger:
  def apply(): GameLogger = GameLoggerImpl()
  
  private class GameLoggerImpl extends GameLogger:
    private var observers: Seq[Observer] = Seq()
    
    override def log(message: String): Unit =
      observers.foreach(_.update(message))

    override def registerObserver(observer: Observer): Unit =
      observers = observers :+ observer
    