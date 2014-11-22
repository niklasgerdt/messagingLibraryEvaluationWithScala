package momeval.subscriber

import grizzled.slf4j.Logging
import org.zeromq.ZMQ
import momeval.simulation.Event

class JeroMqSubscriber(addr: String) extends Subscriber with Logging {
  val ctx = ZMQ.context(1)
  val socket = ctx.socket(ZMQ.SUB)
  socket.connect(addr)
  socket.subscribe("".getBytes)

  override def init() = {
    socket.connect(addr)
    socket.subscribe("".getBytes)
  }

  override def supplyEvent(): Option[Event] = {
    val es = socket.recvStr()
    val e = Event.map(es)
    //    log(e)
    e
  }

  override def tear() = {
    socket.close()
    ctx.term()
  }

  private def log(e: Option[Event]) = e.foreach(ev => info("src: " + ev.src + " dst: " + ev.des + " id:" + ev.id))
}