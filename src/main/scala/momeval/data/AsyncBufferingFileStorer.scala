package momeval.data

import java.io.{FileWriter, BufferedWriter, File}
import grizzled.slf4j.Logging
import momeval.app.Config
import momeval.service.Spawn
import momeval.simulation.Event

object AsyncBufferingFileStorer extends AsyncEventRepo with Flatwile with Logging {
  val QSIZE = Config.MONGOQSIZE

  override def storer(store: String): (Event) => Unit = {
    var events: List[Event] = List.empty

    def flushInsert(es: List[Event]): () => Unit = {
      () => {
        val file = newFile(store)
        info("writing to " + file.getAbsolutePath)
        val out = new BufferedWriter(new FileWriter(path + store))
        es.foreach(e => {
          out.write(e.toString)
          out.newLine()
        })
        out.flush()
        out.close()
      }
    }

    (e: Event) => {
      events = events :+ e
      if (events.size == QSIZE) {
        Spawn.spawn(flushInsert(events.toList))
        events = List.empty
      }
    }
  }
}
