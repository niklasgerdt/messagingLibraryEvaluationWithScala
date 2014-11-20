package momeval.service

object Spawner {

  def spawn(f: () => Unit) = {
    spawnFuture(f).start()
  }
  
  def spawnFuture(f: () => Unit): Thread = {
    new Thread(new Runnable {
      override def run(): Unit = {
        f()
      }
    })
  }
}