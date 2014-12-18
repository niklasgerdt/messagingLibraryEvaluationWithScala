package momeval.application

import grizzled.slf4j.Logging
import momeval.service.Spawn

object JeroMQApp extends App with Logging {
  info("Running JeroMQ simulation")
  assert(args.length == 2)
  val sut = "jero"
  val eventlen = args(0)
  val pauselen = args(1)

  Spawn(() => EventListenerApp(sut))
  Spawn(() => SimulationApp(eventlen, pauselen, sut))
}

object Config {
  // Limit for bulk operations (http://docs.mongodb.org/manual/core/bulk-write-operations/)
  val QSIZE = 1000000

  val FILEQSIZE = 10000
}