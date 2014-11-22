package momeval.app

import grizzled.slf4j.Logging
import momeval.data.AsyncBufferingMongoDbEventRepo
import momeval.subscriber.SubscriberFactory
import momeval.simulation.EventListener

object EventListenerApp extends App with Logging {
  info("Runnnign simulator listeners")
  assert(args.length == 1)
  val updater = AsyncBufferingMongoDbEventRepo.storer("routedEvents")
  val subscriber = args(0)

  List(
    new EventListener(1, updater, SubscriberFactory.stringToSubscriber(subscriber, "tcp://127.0.0.1:6001")),
    new EventListener(2, updater, SubscriberFactory.stringToSubscriber(subscriber, "tcp://127.0.0.1:6001")),
    new EventListener(3, updater, SubscriberFactory.stringToSubscriber(subscriber, "tcp://127.0.0.1:6001")),
    new EventListener(4, updater, SubscriberFactory.stringToSubscriber(subscriber, "tcp://127.0.0.1:6001")))
    .par
    .foreach(s => s.listen)
}
