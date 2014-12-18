package momeval.testers

import com.mongodb.casbah.Imports._

//object MongoDbTester extends App {
object MongoDbTester {
  val mongoClient = MongoClient("localhost", 27017)
  val db = mongoClient("tester")
  val col = db("tester")
  val obj = MongoDBObject("name" -> "Niklas")
  col.insert(obj)
  println("read count from tester.tester " + col.count())
}
