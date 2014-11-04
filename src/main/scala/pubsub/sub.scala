package pubsub

import grizzled.slf4j.Logging
import org.zeromq.ZMQ

import scala.annotation.tailrec

class LocalSub extends Logging{
  var events: List[Notification] = List()
  var eventCount = 0L
  val MILLION: Int = 1000000

  def notify(n: Notification) = {
    eventCount = eventCount + 1
    val e = Notification(Header("", n.header.createdNano, System.nanoTime()), "")
    events = events :+ e
    if (events.size == 10000) {
      asyncLog(events)
      events = List()
    }
  }

  def asyncLog(e: List[Notification]): Unit = {
    new Thread(new Runnable {
      override def run() = for (n <- e) info(n)
    }).start()
  }
}

object JeroMqSub {
  def apply(a: String) = new JeroMqSub(a)
}

class JeroMqSub private(address: String) extends Logging {
  val eventContainer = new EventContainer
  val ctx = ZMQ.context(1)
  val sub = ctx.socket(ZMQ.SUB)

  info("starting up JeroMQ subscriber")
  sub.connect(address)
  sub.subscribe("".getBytes)

  @tailrec final def receive(): Unit = {
    val msg = sub.recvStr()
    debug(msg)
    eventContainer.addEvent(msg)
    receive()
  }
}

class EventContainer {

  case class Event(e: String)

  var events: List[Event] = List()
  var eventCount = 0L

  def addEvent(e: String) = {
    eventCount = eventCount + 1
    events = events :+ Event(e)
  }
}

object JeroMqSubscribers extends App {
  Stream.
    continually(JeroMqSub("tcp://169.254.5.57:5600")).
    take(3).
    par.
    foreach(_.receive())
}

object SimpleJeroMqSub extends App {
  val ctx = ZMQ.context(1)
  val sub = ctx.socket(ZMQ.SUB)
  sub.connect("tcp://169.254.5.57:5600")
  sub.subscribe("".getBytes)

  rec(10)
  println("sub terminated")

  sub.close()
  ctx.term()

  def rec(i: Int): Unit = {
    if (i > 0) {
      val msg = sub.recvStr()
      println("received msg " + i + ":" + msg)
      if (!msg.equalsIgnoreCase("x")) {
        rec(i - 1)
      }
    }
  }
}