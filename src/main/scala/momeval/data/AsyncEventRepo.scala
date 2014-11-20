package momeval.data

import com.mongodb.DBObject
import com.mongodb.casbah.Imports.MongoClient
import com.mongodb.casbah.Imports.MongoCollection
import com.mongodb.casbah.commons.MongoDBObject
import momeval.Event
import grizzled.slf4j.Logging
import momeval.service.Spawner

trait AsyncEventRepo {
  this: DataBase =>

  def storer(store: String): Event => Unit
}

//------------------------------------------------------------------------------------------------------------------------------------

object AsyncBufferingMongoDbEventRepo extends AsyncEventRepo with MongoDB with Logging {
  import com.mongodb.casbah.commons.MongoDBObject
  import com.mongodb.DBObject

  val QSIZE = 10000

  override def storer(store: String): Event => Unit = {
    val col = initNewCollection(store)
    var events: List[Event] = List.empty

    def flushInsert(es: List[Event]): () => Unit = {
      () =>
        {
          val bulk = col.initializeOrderedBulkOperation
          es.foreach(e => {
            val mo = map(e)
            bulk.insert(mo)
          })
          val res = bulk.execute()
          info("Inserted: " + res.getInsertedCount)
        }
    }

    (e: Event) =>
      {
        events = events :+ e
        if (events.size == QSIZE) {
          Spawner.spawn(flushInsert(events.toList))
          flushInsert(events.toList)
          events = List.empty
        }
      }
  }

  private def map(e: Event): DBObject = {
    val mob = MongoDBObject.newBuilder
    mob += "ID" -> e.id
    mob += "SRC" -> e.src
    mob += "SENT" -> e.created
    mob += "DEST" -> e.des
    mob += "ROUTED" -> e.routed
    mob.result
  }
}
