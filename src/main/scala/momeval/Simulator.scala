package momeval

import momeval.service.{ Kill, Pauser }
import momeval.publisher.Publisher
import grizzled.slf4j.Logging

class Simulator(eventSupplier: () => Option[Event], publisher: Publisher, pauser: () => Unit, storeEvent: Event => Unit, kill: () => Boolean) extends Logging {

  def simulate(): Unit = {
    info("starting simulation")
    Stream
      .continually(eventSupplier())
      .takeWhile(o => o.isDefined)
      .foreach(e => foreach(e.get))
  }

  def mapToKill(e: Event): Boolean = kill()

  def foreach(e: Event): Unit = {
    publisher.publish(e)
    storeEvent(e)
    pauser()
  }
}

