package momeval.simulation

import grizzled.slf4j.Logging
import momeval.subscriber.Subscriber

class EventListener(id: Int, update: Event => Unit, sub: Subscriber) extends Logging {
  def listen(): Unit = {
    info("starting simulationlistener")
    Stream
      .continually(sub.supplyEvent())
      .takeWhile(o => o.isDefined)
      .foreach(e => updateEvent(e.get))
  }

  private def updateEvent(e: Event) {
    val ev = Event.copyWithDestination(e, id)
//    info("src: " + ev.src + " dst: " + ev.des + " id:" + ev.id)
    update(ev)
  }
}
