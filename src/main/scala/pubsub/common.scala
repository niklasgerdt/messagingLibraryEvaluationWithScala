package pubsub

import scala.annotation.tailrec

case class Notification(header: Header, body: String)

case class Header(id: String, createdNano: Long, routedNano: Long)

object Util {

  @tailrec def pause(nanoTime: Long, pauseTime: Long): Unit = {
    if (System.nanoTime < nanoTime + pauseTime)
      pause(nanoTime, pauseTime)
  }
}
