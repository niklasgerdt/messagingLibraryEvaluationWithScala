package momeval.app

import momeval.service.Spawn

object JeroMQApp extends App {
  assert(args.length == 2)
  val sut = "jero"
  val eventlen = args(0)
  val pauselen = args(1)

  Spawn(() => EventListenerApp(sut))
  Spawn(() => SimulationApp(eventlen, pauselen, sut))
}

object Config {
  val QSIZE = 100000
}