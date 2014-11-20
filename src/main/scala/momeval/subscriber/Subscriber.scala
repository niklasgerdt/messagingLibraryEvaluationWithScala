package momeval.subscriber

import momeval.simulation.Event

trait Subscriber {
  def init() = {}

  def supplyEvent(): Option[Event]

  def tear() = {}
}

//-----------------------------------------------------------------------------------------------------------------------------------------------------------

class JeroMqSubscriber(addr: String) extends Subscriber {
  import org.zeromq.ZMQ

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