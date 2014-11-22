package momeval.app

import momeval.data.AsyncBufferingMongoDbEventRepo
import momeval.service.{ Pauser, Kill }
import grizzled.slf4j.Logging
import momeval.implicits.Implicits._
import momeval.publisher.PublisherFactory
import momeval.simulation.Event
import momeval.simulation.Simulator

object SimulationApp extends App with Logging {
  info("Runnnign simulators with " + args(0) + " and " + args(1) + args(3))
  assert(args.length == 3)
  val eventlen = args(0)
  val pauselen = args(1)
  val publisher = args(2)

  val pub1 = PublisherFactory.stringToPublisher(publisher, "tcp://127.0.0.1:5001")
  val pub2 = PublisherFactory.stringToPublisher(publisher, "tcp://127.0.0.1:5002")
  val pub3 = PublisherFactory.stringToPublisher(publisher, "tcp://127.0.0.1:5003")
  val pub4 = PublisherFactory.stringToPublisher(publisher, "tcp://127.0.0.1:5004")
  val storer = AsyncBufferingMongoDbEventRepo.storer("sentEvents")
  val pauser = Pauser.pauseFunction(pauselen)
  val kill = Kill.asSignalListener()

  List(
    new Simulator(Event.supplier(1, eventlen, kill), pub1, pauser, storer, kill),
    new Simulator(Event.supplier(2, eventlen, kill), pub2, pauser, storer, kill),
    new Simulator(Event.supplier(3, eventlen, kill), pub3, pauser, storer, kill),
    new Simulator(Event.supplier(4, eventlen, kill), pub4, pauser, storer, kill))
    .par
    .foreach(_.simulate())
}