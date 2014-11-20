package momeval.simulation

import momeval.data.AsyncBufferingMongoDbEventRepo
import momeval.service.Pauser
import momeval.service.Kill
import momeval.publisher.JeroMqPublisher
import grizzled.slf4j.Logging
import momeval.implicits.Implicits._
import momeval.publisher.PublisherFactory

object Simulation extends App with Logging {
  info("Runnnign simulators with " + args(0) + " and " + args(1) + args(3))
  val EVENTLEN = args(0)
  val PAUSELEN = args(1)
  val publisher = args(3)
  val pub1 = PublisherFactory.stringToPublisher(publisher, "tcp://127.0.0.1:5001")
  val pub2 = PublisherFactory.stringToPublisher(publisher, "tcp://127.0.0.1:5002")
  val pub3 = PublisherFactory.stringToPublisher(publisher, "tcp://127.0.0.1:5003")
  val pub4 = PublisherFactory.stringToPublisher(publisher, "tcp://127.0.0.1:5004")

  val storer = AsyncBufferingMongoDbEventRepo.storer("sentEvents")
  val pauser = Pauser.pauseFunction(PAUSELEN)
  val kill = Kill.asSignalListener()

  List(
    new Simulator(Event.supplier(1, EVENTLEN, kill), pub1, pauser, storer, kill),
    new Simulator(Event.supplier(2, EVENTLEN, kill), pub2, pauser, storer, kill),
    new Simulator(Event.supplier(3, EVENTLEN, kill), pub3, pauser, storer, kill),
    new Simulator(Event.supplier(4, EVENTLEN, kill), pub4, pauser, storer, kill))
    .par
    .foreach(_.simulate())
}