package momeval.data

import momeval.simulation.Event
import grizzled.slf4j.Logging
import momeval.service.Spawn
import momeval.app.Config
import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.DBObject

object AsyncBufferingMongoDbEventRepo extends AsyncEventRepo with MongoDB with Logging {
  val QSIZE = Config.MONGOQSIZE

  override def storer(store: String): Event => Unit = {
    val col = if (store.equals("sentEvents")) sentEventCollection() else if (store.equals("sentEvents")) routedEVentCollection() else newCollection(store)
    info("writing events to " + col.name)
    var events: List[Event] = List.empty

    def map(e: Event): DBObject = {
      val mob = MongoDBObject.newBuilder
      mob += "ID" -> e.id
      mob += "SRC" -> e.src
      mob += "SENT" -> e.created
      mob += "DEST" -> e.des
      mob += "ROUTED" -> e.routed
      mob.result
    }

    def flushInsert(es: List[Event]): () => Unit = {
      () => {
        val bulk = col.initializeUnorderedBulkOperation
        es.foreach(e => {
          val mo = map(e)
          bulk.insert(mo)
        })
        val res = bulk.execute()
        info("Inserted to " + store + "(" + es.head.src + "/" + es.head.des + "): " + res.getInsertedCount)
      }
    }

    def log(e: Event) {
      if (e.src == 1 && e.des != 0)
        info("src: " + e.src + " dst: " + e.des + " id:" + e.id + " stored: " + events.size)
    }

    (e: Event) => {
      events = events :+ e
      //      log(e)
      if (events.size == QSIZE) {
        Spawn.spawn(flushInsert(events.toList))
        events = List.empty
      }
    }
  }
}
