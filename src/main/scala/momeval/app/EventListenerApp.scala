package momeval.app

import grizzled.slf4j.Logging
import momeval.data.AsyncBufferingMongoDbEventRepo
import momeval.subscriber.SubscriberFactory
import momeval.simulation.EventListener

object EventListenerApp extends Logging {

  def apply(subscriber: String) = {
    info("Runnnign simulator listeners")
    val updater = AsyncBufferingMongoDbEventRepo.storer("routedEvents")

    List(
      new EventListener(1, updater, SubscriberFactory.stringToSubscriber(subscriber, "tcp://168.1.1.1:6001")),
      new EventListener(2, updater, SubscriberFactory.stringToSubscriber(subscriber, "tcp://168.1.1.1:6001")),
      new EventListener(3, updater, SubscriberFactory.stringToSubscriber(subscriber, "tcp://168.1.1.1:6001")),
      new EventListener(4, updater, SubscriberFactory.stringToSubscriber(subscriber, "tcp://168.1.1.1:6001")))
      .par
      .foreach(s => s.listen)
  }
}
