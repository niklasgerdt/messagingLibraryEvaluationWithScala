package simulation

import java.io.File

import grizzled.slf4j.Logging
import pubsub.{Header, Notification, Util}

import scala.annotation.tailrec
import scala.util.Random

case class SimulatorConfig(notifications: Int, notificationLength: Int, pauseTime: Long, pub: Notification => Unit, end: () => Boolean)

trait SimConfValues {
  val ENDLESS = -1
  val milliInNanos = 1000000
  val mikroInNanos = 1000
  val shortMsg = 100
  val mediumMsg = 1000
  val longMsg = 100000
  val nano = 1
  val tenmillion = 10000000
  val tenthousand = 10000
  val hundred = 100
  val pauseForMillionMsgsPerSec = 1000L
  val pauseForTenThousandMsgsPerSec = 100000L
  val nil = 0L
}

object KillFile extends Logging {
  var killFile = false

  new Thread(new Runnable {
    override def run(): Unit = while (true) {
      val f = new File("kill")
      if (f.exists()){
        killFile = true
        f.delete()
      }
      Thread.sleep(1000)
    }
  }).start()

  def kill() = killFile
}

trait EndSignals extends Logging {

  def endless() = false

}

trait SimulatorMapper extends SimConfValues with Logging {

  def map(c: SimulatorConfig): () => Unit = {
    () => {
      @tailrec def simulate(toGo: Int): Unit = {
        if ((c.notifications == ENDLESS || toGo > 0) && !c.end()) {
          val h = Header("", System.nanoTime, 0L)
          val n = Notification(h, msg)
          debug(n)
          c.pub(n)
          Util.pause(System.nanoTime, c.pauseTime)
          simulate(toGo - 1)
        }
      }

      info("simulating " + c.notifications + " msgs, " + c.notificationLength + " len, " + c.pauseTime + " pause")
      lazy val msg = Random.nextString(c.notificationLength)
      simulate(c.notifications)
      info("done simulating")
    }
  }
}

trait SimulatorRunner extends Logging {

  def runParallel(s: List[() => Unit]): Unit = {
    info("running " + s.size + " simulators")
    s.par.foreach(_())
    info("simulations done")
  }
}