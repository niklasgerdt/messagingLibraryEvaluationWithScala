package momeval.data

import com.mongodb.DBObject
import com.mongodb.casbah.Imports.MongoClient
import com.mongodb.casbah.Imports.MongoCollection
import com.mongodb.casbah.commons.MongoDBObject
import momeval.Event
import grizzled.slf4j.Logging

trait AsyncEventRepo {
  this: DataBase =>

  def storer(): Event => Unit
  def updater(): Event => Unit
}

//---------------------------------------------------------------------------------------------------------------

object AsyncBufferingMongoDbEventRepo extends AsyncEventRepo with MongoDB with Logging {
  import com.mongodb.casbah.commons.MongoDBObject
  import com.mongodb.DBObject

  val QSIZE = 10000
  val col = initNewCollection("events")

  override def storer(): Event => Unit = {
    var events: List[Event] = List.empty
    (e: Event) =>
      {
        events = events :+ e
        if (events.size == QSIZE) {
          flushInsert(events.toList)
          events = List.empty
        }
      }
  }

  override def updater(): Event => Unit = {
    var events: List[Event] = List.empty
    (e: Event) =>
      {
        events = events :+ e
        if (events.size == 10 * QSIZE) {
          flushUpdate(events.toList)
          events = List.empty
        }
      }
  }

  private def flushInsert(es: List[Event]): Unit = {
    new Thread(new Runnable() {
      override def run() {
        val bulk = col.initializeOrderedBulkOperation
        es.foreach(e => {
          val mo = map(e)
          bulk.insert(mo)
        })
        val res = bulk.execute()
        info("Inserted: " + res.getInsertedCount)
      }
    }).start()
  }

  private def flushUpdate(es: List[Event]): Unit = {
    info("flushing updates")
    new Thread(new Runnable() {
      override def run() {
        val bulk = col.initializeOrderedBulkOperation
        es.foreach(e => {
          val mof = MongoDBObject.newBuilder
          mof += "ID" -> e.id
          mof += "SRC" -> e.src
          val up = bulk.find(mof.result)
          val moup = MongoDBObject.newBuilder
          moup += "ID" -> e.id
          moup += "SRC" -> e.src
          moup += "SENT" -> e.created
          moup += "DEST" -> e.des
          moup += "ROUTED" -> e.routed
          up.replaceOne(moup.result)
        })
        val res = bulk.execute()
        info("Updated: " + res.getModifiedCount.getOrElse(0))
      }
    }).start()
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
