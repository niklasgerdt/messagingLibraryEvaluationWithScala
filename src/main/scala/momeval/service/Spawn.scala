package momeval.service

object Spawn {

  def apply(f: () => Unit) = spawn(f)

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