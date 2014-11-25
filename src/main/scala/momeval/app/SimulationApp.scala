package momeval.app

import momeval.data.{AsyncBufferingFileStorer, AsyncBufferingMongoDbEventRepo}
import momeval.service.{Pauser, Kill}
import grizzled.slf4j.Logging
import momeval.implicits.Implicits._
import momeval.publisher.PublisherFactory
import momeval.simulation.Event
import momeval.simulation.Simulator

object SimulationApp extends Logging {

  def apply(eventlen: String, pauselen: String, publisher: String) = {
    def pub(address: String) = PublisherFactory.stringToPublisher(publisher, address)
    val pauser = Pauser.pauseFunction(pauselen)
//    def storer() = AsyncBufferingMongoDbEventRepo.storer("sentEvents")
    def storer() = AsyncBufferingFileStorer.storer("sentEvents")

    val kill = Kill.asSignalListener()
    def supplier(id: Int) = Event.supplier(id, eventlen, kill)

    List(
      new Simulator(supplier(1), pub("tcp://168.1.1.2:5001"), pauser, storer(), kill),
      new Simulator(supplier(2), pub("tcp://168.1.1.2:5002"), pauser, storer(), kill),
      new Simulator(supplier(3), pub("tcp://168.1.1.2:5003"), pauser, storer(), kill),
      new Simulator(supplier(4), pub("tcp://168.1.1.2:5004"), pauser, storer(), kill))
      .par
      .foreach(_.simulate())
  }
}
