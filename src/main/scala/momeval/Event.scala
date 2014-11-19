package momeval

case class Event(id: Long, src: Int, des: Int, created: Long, routed: Long, content: String)

object Event {

  def supplier(src: Int, contentLength: Int, end: () => Boolean): () => Option[Event] = {
    var id = 0

    val a = Array.fill(contentLength)("A")
    val content = a.toString

    () => {
      if (end())
        None
      else {
        id = id + 1
        Some(Event(id, src, 0, System.nanoTime(), 0, content))
      }
    }
  }

  def copyWithDestination(e: Event, dst: Int) = {
    Event(e.id, e.src, dst, e.created, System.nanoTime(), e.content)
  }

  def map(es: String): Option[Event] = {
    val r = es.split("\\(")(1).split("\\)")(0).split(",")
    if (r.size == 5)
      Some(Event(r(0), r(1), r(2), r(3), r(4), ""))
    else if (r.size == 6)
      Some(Event(r(0), r(1), r(2), r(3), r(4), r(5)))
    else
      None
  }

  implicit def stringToLong(s: String): Long = augmentString(s).toLong

  implicit def stringToInt(s: String): Int = augmentString(s).toInt
}
