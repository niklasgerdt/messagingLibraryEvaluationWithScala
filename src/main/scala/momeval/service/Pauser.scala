package momeval.service

import scala.annotation.tailrec

object Pauser {

  def pauseFunction(pauseLen: Long): () => Unit = {

    @tailrec def recursivePause(initTime: Long): Unit =
      if (System.nanoTime < initTime + pauseLen)
        recursivePause(initTime)

    () => recursivePause(System.nanoTime())
  }
}
