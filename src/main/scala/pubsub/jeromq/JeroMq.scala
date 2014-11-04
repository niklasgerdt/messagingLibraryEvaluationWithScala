package pubsub.jeromq

import org.zeromq.ZMQ
import scala.annotation.tailrec

object JeroMq {
  val ctx = ZMQ.context(1)
  val pub = ctx.socket(ZMQ.PUB)
  val sub = ctx.socket(ZMQ.SUB)
  var kill = 0
  val hook = new Thread(new Runnable {
    override def run(): Unit = {
      println("term sig received");
      kill = -1
      wait(1);
    }
  })

  //  sub.connect("tcp://168.1.1.1:5001")
  //  sub.connect("tcp://168.1.1.1:5002")
  //  sub.connect("tcp://168.1.1.1:5003")
  //  sub.connect("tcp://168.1.1.1:5004")
  //  sub.subscribe("".getBytes)
  //  pub.bind("tcp://168.1.1.2:6001")
  //  println("JeroMQ ctx up");

  sub.connect("tcp://localhost:5001")
  sub.connect("tcp://localhost:5002")
  sub.connect("tcp://localhost:5003")
  sub.connect("tcp://localhost:5004")
  sub.subscribe("".getBytes)
  pub.bind("tcp://*:6001")
  println("JeroMQ ctx up");

  Runtime.getRuntime().addShutdownHook(hook);
  recSend()

  sub.close()
  pub.close()
  ctx.term()
  println("JeroMQ ctx down");

  @tailrec final def recSend(): Unit = {
    val msg = sub.recvStr()
    if (!sub.hasReceiveMore()) {
      println(msg)
      pub.send(msg)
    }
    if (kill == 0)
      recSend()
  }
}
