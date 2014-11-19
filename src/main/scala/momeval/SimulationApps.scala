package momeval

import momeval.data.AsyncBufferingMongoDbEventRepo
import momeval.publisher.Publisher
import momeval.service.Pauser
import momeval.service.Kill
import momeval.publisher.JeroMqPublisher
import grizzled.slf4j.Logging

object JeroMqSimulation extends App with Logging {
  info("Runnnign simulators with " + args(0) + " and " + args(1))
  val EVENTLEN = args(0)
  val PAUSELEN = args(1)

  val storer = AsyncBufferingMongoDbEventRepo.storer("sentEvents")
  val pauser = Pauser.pauseFunction(PAUSELEN)
  val kill = Kill.asSignalListener()

  List(
    new Simulator(Event.supplier(1, EVENTLEN, kill), new JeroMqPublisher("tcp://127.0.0.1:5001"), pauser, storer, kill),
    new Simulator(Event.supplier(2, EVENTLEN, kill), new JeroMqPublisher("tcp://127.0.0.1:5002"), pauser, storer, kill),
    new Simulator(Event.supplier(3, EVENTLEN, kill), new JeroMqPublisher("tcp://127.0.0.1:5003"), pauser, storer, kill),
    new Simulator(Event.supplier(4, EVENTLEN, kill), new JeroMqPublisher("tcp://127.0.0.1:5004"), pauser, storer, kill))
    .par
    .foreach(_.simulate())

  //todo: GLOBAL
  implicit def stringToLong(s: String): Long = augmentString(s).toLong
  //todo: GLOBAL
  implicit def stringToInt(s: String): Int = augmentString(s).toInt
}