package simulation

import grizzled.slf4j.Logging
import pubsub._

object DummySimulation extends App with Logging with SimConfValues with DummyPub with SimulatorMapper with SimulatorRunner with EndSignals {
  info("Run 3 simulations with different configurations notification sizes and counts. Duration of simulation ~ 1s.")
  runParallel(
    List(SimulatorConfig(tenmillion / 10, hundred, pauseForMillionMsgsPerSec, pub, endless),
      SimulatorConfig(tenthousand, tenthousand, pauseForTenThousandMsgsPerSec, pub, endless),
      SimulatorConfig(tenthousand, hundred * 10, pauseForTenThousandMsgsPerSec, pub, endless)
    ).map(map(_))
  )
}

object EndlessDummySimulation extends App with Logging with SimConfValues with DummyPub with SimulatorMapper with SimulatorRunner with EndSignals {
  info("Run 3 simulations with different configurations notification sizes and counts. Infinite simulation.")
  runParallel(
    List(SimulatorConfig(ENDLESS, hundred, pauseForMillionMsgsPerSec, pub, endless),
      SimulatorConfig(ENDLESS, tenthousand, pauseForTenThousandMsgsPerSec, pub, endless),
      SimulatorConfig(ENDLESS, hundred * 10, pauseForTenThousandMsgsPerSec, pub, endless)
    ).map(map(_))
  )
}

object JeroMqSimulation extends App with Logging with SimConfValues with SimulatorMapper with SimulatorRunner with EndSignals {
  info("Run simulations with different configurations")
  val pub1 = JeroMqPub("tcp://169.254.5.233:5500")
  val pub2 = JeroMqPub("tcp://169.254.5.233:5501")
  val pub3 = JeroMqPub("tcp://169.254.5.233:5502")
  runParallel(
    List(SimulatorConfig(ENDLESS, hundred, pauseForMillionMsgsPerSec, pub1.pub, endless),
      SimulatorConfig(ENDLESS, tenthousand, pauseForTenThousandMsgsPerSec, pub2.pub, endless),
      SimulatorConfig(ENDLESS, hundred * 10, pauseForTenThousandMsgsPerSec, pub3.pub, endless)
    ).map(map(_))
  )
  pub1.end()
  pub2.end()
  pub3.end()
}

object EndlessJeroMqSimulation extends App with Logging with SimConfValues with SimulatorMapper with SimulatorRunner with EndSignals {
  info("Run simulations with different configurations")
  val pub = JeroMqPub("tcp://169.254.5.233:5500")
  runParallel(
    List(SimulatorConfig(ENDLESS, hundred, 0, pub.pub, endless))
      .map(map(_))
  )
  pub.end()
}

object LocalSimulation1 extends App with SimulatorMapper with SimulatorRunner with SimConfValues {
  LocalPubSub.subscribe(new LocalSub)
  LocalPubSub.subscribe(new LocalSub)
  LocalPubSub.subscribe(new LocalSub)

  runParallel(
    List(SimulatorConfig(ENDLESS, shortMsg, milliInNanos, LocalPub.pub, KillFile.kill),
      SimulatorConfig(ENDLESS, shortMsg, milliInNanos, LocalPub.pub, KillFile.kill),
      SimulatorConfig(ENDLESS, shortMsg, milliInNanos, LocalPub.pub, KillFile.kill)).
      map(map(_))
  )
}