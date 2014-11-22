package momeval.publisher

import org.zeromq.ZMQ
import momeval.simulation.Event

class JeroMqPublisher(addr: String) extends Publisher {
  val ctx = ZMQ.context(1)
  val socket = ctx.socket(ZMQ.PUB)
  socket.bind(addr)

  override def init() = {
    socket.bind(addr)
  }

  override def publish(e: Event) = {
    socket.send(e.toString)
  }

  override def tear() = {
    socket.close()
    ctx.term()
  }
}