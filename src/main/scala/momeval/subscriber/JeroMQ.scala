package momeval.subscriber

import org.zeromq.ZMQ
import momeval.simulation.Event

class JeroMqSubscriber(addr: String) extends Subscriber {
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
    Event.map(es)
  }

  override def tear() = {
    socket.close()
    ctx.term()
  }
}