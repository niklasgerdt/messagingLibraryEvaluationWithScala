package momeval.simulation

import momeval.application.Config
import momeval.publisher.Publisher
import grizzled.slf4j.Logging

class Simulator(eventSupplier: () => Option[Event], publisher: Publisher, pauser: () => Unit, storeEvent: Event => Unit, kill: () => Boolean) extends Logging {
  val limit = Config.QSIZE

  def simulate(): Unit = {
    info("starting simulation")
    Stream
      .continually(eventSupplier())
      .takeWhile(o => o.isDefined)
      .takeWhile(o => o.get.id < limit)
      .foreach(e => foreach(e.get))
  }

  def foreach(e: Event): Unit = {
    publisher.publish(e)
    storeEvent(e)
    pauser()
  }
}

