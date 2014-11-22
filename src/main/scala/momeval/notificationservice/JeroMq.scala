package momeval.notificationservice

import org.zeromq.ZMQ
import scala.annotation.tailrec
import grizzled.slf4j.Logging
import momeval.service.Spawner

object JeroMqNotificationService extends App with Logging {
  val ctx = ZMQ.context(1)
  val pub = ctx.socket(ZMQ.PUB)
  val sub = ctx.socket(ZMQ.SUB)
  var kill = 0

  addShutdownHook()

    sub.connect("tcp://168.1.1.1:5001")
    sub.connect("tcp://168.1.1.1:5002")
    sub.connect("tcp://168.1.1.1:5003")
    sub.connect("tcp://168.1.1.1:5004")
    sub.subscribe("".getBytes)
    pub.bind("tcp://168.1.1.2:6001")

 // sub.connect("tcp://127.0.0.1:5001")
  //sub.connect("tcp://127.0.0.1:5002")
 // sub.connect("tcp://127.0.0.1:5003")
 // sub.connect("tcp://127.0.0.1:5004")
  //sub.subscribe("".getBytes)
  //pub.bind("tcp://127.0.0.1:6001")

  println("JeroMQ ctx up");

  recSend()

  sub.close()
  pub.close()
  ctx.term()
  println("JeroMQ ctx down");

  @tailrec final def recSend(): Unit = {
    val msg = sub.recvStr()
    //    info("sending msg " + msg)
    pub.send(msg)
    if (kill == 0)
      recSend()
  }

  private def addShutdownHook() {
    val hook = () => {
      println("term sig received")
      kill = -1
    }
    val thread = Spawner.spawnFuture(hook)
    Runtime.getRuntime().addShutdownHook(thread);
  }
}
