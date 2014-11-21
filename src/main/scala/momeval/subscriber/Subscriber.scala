package momeval.subscriber

import momeval.simulation.Event

trait Subscriber {
  def init() = {}

  def supplyEvent(): Option[Event]

  def tear() = {}
}

object DummySub extends Subscriber {
  override def supplyEvent() = Some(Event(0, 0, 0, 0, 0, ""))
}
object SubscriberFactory {
  def stringToSubscriber(s: String, address: String): Subscriber =
    s match {
      case "jero" => new JeroMqSubscriber(address)
      case _ => DummySub
    }
}

