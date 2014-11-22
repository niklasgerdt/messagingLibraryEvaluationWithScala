package momeval.app

import momeval.data.AsyncBufferingMongoDbEventRepo
import momeval.service.{ Pauser, Kill }
import grizzled.slf4j.Logging
import momeval.implicits.Implicits._
import momeval.publisher.PublisherFactory
import momeval.simulation.Event
import momeval.simulation.Simulator

object SimulationApp extends Logging {

  def apply(eventlen: String, pauselen: String, publisher: String) = {
    val pub1 = PublisherFactory.stringToPublisher(publisher, "tcp://168.1.1.2:5001")
    val pub2 = PublisherFactory.stringToPublisher(publisher, "tcp://168.1.1.2:5002")
    val pub3 = PublisherFactory.stringToPublisher(publisher, "tcp://168.1.1.2:5003")
    val pub4 = PublisherFactory.stringToPublisher(publisher, "tcp://168.1.1.2:5004")
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
}
