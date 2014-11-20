package momeval.data

import momeval.simulation.Event

trait DataBase

trait MongoDB extends DataBase {
  import com.mongodb.casbah.Imports._

  val mongoClient = MongoClient("localhost", 27017)
  val db = mongoClient("eventdb")

  def initNewCollection(colName: String): MongoCollection = {
    if (db.collectionExists(colName))
      db(colName).drop
    db(colName)
  }
  
  
}
