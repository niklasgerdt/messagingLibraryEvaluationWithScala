package momeval.publisher

import momeval.simulation.Event

trait Publisher {
  def init() = {}

  def publish(e: Event) = {}

  def tear() = {}
}

object DummyPub extends Publisher
object PublisherFactory {
  def stringToPublisher(s: String, address: String): Publisher =
    s match {
      case "jero" => new JeroMqPublisher(address)
      case _ => DummyPub
    }
}

