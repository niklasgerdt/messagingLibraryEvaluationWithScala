package pubsub

import grizzled.slf4j.Logging
import org.zeromq.ZMQ

trait Pub {
  def pub(n: Notification): Unit
}

trait DummyPub extends Pub {
  override def pub(n: Notification): Unit = {}
}

trait LoggingPub extends Pub with Logging {
  override def pub(n: Notification): Unit = info(n)
}

object LocalPub extends Pub {
  override def pub(n: Notification) = LocalPubSub.publish(n)
}

class JeroMqPub private(addr: String) extends Pub with Logging {
  val ctx = ZMQ.context(1)
  val socket = ctx.socket(ZMQ.PUB)
  socket.bind(addr)

  override def pub(n: Notification) = {
    debug("publishing notification")
    socket.send(n.toString)
  }

  def end() = {
    socket.close()
    ctx.term()
  }
}

object JeroMqPub {
  def apply(addr: String) = new JeroMqPub(addr)
}

object SimpleJeroMqPub extends App {
  val ctx = ZMQ.context(1)
  val pub = ctx.socket(ZMQ.PUB)
  pub.bind("tcp://169.254.5.233:5500")

  println("send event (Max 10 events, x/X terminates)")
  run(10)

  pub.close()
  ctx.term()

  def run(i: Int): Unit = {
    if (i > 0) {
      val x = Console.in.readLine()
      pub.send(x)
      if (!x.equalsIgnoreCase("x")) {
        run(i - 1)
      }
    }
  }
}