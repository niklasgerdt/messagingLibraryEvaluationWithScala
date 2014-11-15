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
}
