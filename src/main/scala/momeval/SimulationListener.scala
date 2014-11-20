package momeval

import grizzled.slf4j.Logging
import momeval.data.AsyncBufferingMongoDbEventRepo
import momeval.subscriber.Subscriber
import momeval.subscriber.JeroMqSubscriber

object SimulationListenerApp extends App with Logging {
  info("Runnnign simulator listeners")

  val updater = AsyncBufferingMongoDbEventRepo.storer("routedEvents")

  List(
    new SimulationListener(1, updater, new JeroMqSubscriber("tcp://127.0.0.1:6001")),
    new SimulationListener(2, updater, new JeroMqSubscriber("tcp://127.0.0.1:6001")),
    new SimulationListener(3, updater, new JeroMqSubscriber("tcp://127.0.0.1:6001")),
    new SimulationListener(4, updater, new JeroMqSubscriber("tcp://127.0.0.1:6001")))
    .par
    .foreach(s => s.listen)
}

class SimulationListener(id: Int, update: Event => Unit, sub: Subscriber) extends Logging {
  def listen(): Unit = {
    info("starting simulationlistener")
    Stream
      .continually(sub.supplyEvent())
      .takeWhile(o => o.isDefined)
      .foreach(e => updateEvent(e.get))
  }

  private def updateEvent(e: Event) {
    val ec = Event.copyWithDestination(e, id)
    update(ec)
  }
}
