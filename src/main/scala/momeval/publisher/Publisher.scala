package momeval.publisher

import momeval.simulation.Event

trait Publisher {
  def init() = {}

  def publish(e: Event) = {}

  def tear() = {}
}

class JeroMqPublisher(addr: String) extends Publisher {
  import org.zeromq.ZMQ

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