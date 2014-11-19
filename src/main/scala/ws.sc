object ws {
  println("Welcome to the Scala worksheet")       //> Welcome to the Scala worksheet

  import momeval.Event

  val e = Event(0, 0, 0, 0, 0, "")                //> e  : momeval.Event = Event(0,0,0,0,0,)

  println(e.toString)                             //> Event(0,0,0,0,0,)

  val es = e.toString                             //> es  : String = Event(0,0,0,0,0,)

  val r = es.split("\\(")                         //> r  : Array[String] = Array(Event, 0,0,0,0,0,))
  val rr = r(1).split("\\)")                      //> rr  : Array[String] = Array(0,0,0,0,0,)
  val rrr = rr(0).split(",")                      //> rrr  : Array[String] = Array(0, 0, 0, 0, 0)

}