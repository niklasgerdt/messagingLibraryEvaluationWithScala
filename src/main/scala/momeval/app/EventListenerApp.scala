package momeval.app

import grizzled.slf4j.Logging
import momeval.data.AsyncBufferingMongoDbEventRepo
import momeval.subscriber.SubscriberFactory
import momeval.simulation.EventListener

object EventListenerApp extends Logging {

  def apply(subscriber: String) = {
    info("Runnign simulator listeners")
    def storer() = AsyncBufferingMongoDbEventRepo.storer("routedEvents")
    def subs() = SubscriberFactory.stringToSubscriber(subscriber, "tcp://168.1.1.1:6001")

    List(
      new EventListener(1, storer(), subs()),
      new EventListener(2, storer(), subs()),
      new EventListener(3, storer(), subs()),
      new EventListener(4, storer(), subs()))
      .par
      .foreach(s => s.listen)
  }
}
