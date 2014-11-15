package momeval.service

object Kill {
  def asNeverKill() = () => false

  def asSignalListener() = {
    var kill = false
    Runtime.getRuntime().addShutdownHook(new Thread {
      override def run() {
        kill = true
      }
    });

    () => kill
  }

  def asKillFileListener() = {
    import java.io.File

    val KILLFILE = "kill"
    var kill = false

    new Thread(new Runnable {
      override def run() {
        while (true) {
          val f = new File(KILLFILE)
          if (f.exists()) {
            kill = true
            f.delete()
          }
          Thread.sleep(100)
        }
      }
    }).start()

    () => kill
  }
}