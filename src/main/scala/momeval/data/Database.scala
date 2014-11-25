package momeval.data

trait DataBase

trait MongoDB extends DataBase {

  import com.mongodb.casbah.Imports._

  val mongoClient = MongoClient("localhost", 27017)
  mongoClient.dropDatabase("eventdb")
  val db = mongoClient("eventdb")
  val sentEvents = db("sentEvents")
  val routedEvents = db("routedEvents")

  def sentEventCollection() = sentEvents

  def routedEVentCollection() = routedEvents

  def newCollection(c: String) = db(c)
}

trait Flatwile extends DataBase {

  import java.io.File

  val path = "./"

  def newFile(name: String) = new File(path + name + System.nanoTime())
}
