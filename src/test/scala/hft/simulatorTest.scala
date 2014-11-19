package hft

import org.scalatest._
import pubsub.{ Pub, Notification }

class SimulatorTest extends BaseTest {
  def mock = new Mock

  "Simulator" should "publish notifications" in {

    //  it should "not send notifications if no confs" in {
    //    val m = mock
    //    val c = List()
    //    m.start(Simulator.sims(c))
    //    m.events.size should be(0)
    //  }
    //
    //  it should "send notifications as conffed value" in {
    //    val m = mock
    //    val c = List(SimulatorConfig(3, 1, 1), SimulatorConfig(2, 1, 1))
    //    m.start(Simulator.sims(c))
    //    m.events.size should be(3 + 2)
    //  }
    //
    //  it should "send notification as conffed length" in {
    //    val m = mock
    //    val c = List(SimulatorConfig(3, 5, 1), SimulatorConfig(2, 2, 1))
    //    m.start(Simulator.sims(c))
    //    val f = m.events.filter(_.body.length == 5)
    //    f.size should be(3)
    //  }
    //
    //  it should "pause between notifications if conffed" in {
    //    val m1 = mock
    //    val c1 = List(SimulatorConfig(2, 0, 1000000))
    //    m1.start(Simulator.sims(c1))
    //    val f1 = (m1.events :\ 0L)(_.header.get.createdNano - _)
    //    val m2 = mock
    //    val c2 = List(SimulatorConfig(2, 0, 0))
    //    m2.start(Simulator.sims(c2))
    //    val f2 = (m2.events :\ 0L)(_.header.get.createdNano - _)
    //    -f1 should be > 1000000L
    //    -f2 should be < 1000000L
  }

  class Mock extends Pub {
    var events: List[Notification] = List()

    override def pub(n: Notification) = {
      events = events :+ n
    }
  }

}

