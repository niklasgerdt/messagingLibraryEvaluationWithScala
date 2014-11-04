package anal

import eu.route20.hft.test.BaseTest
import r20.anal.{Util, Stamp}

class RouteInfoTest extends BaseTest {

  "to" should "convert string to RouteInfo" in {
    val s = "DXXX;;1000000;1413397527.676847947;1413397527.676848001;0.000000000"
    val r = Util.mapToRouteInfo(s)
    assert(r.src == "DXXX")
    assert(r.dst == "")
    assert(r.id == 1000000)
  }
}


class StampTest extends BaseTest {

  "apply" should "drop invalid chars" in {
    intercept[AssertionError] {
      Util.mapToStamp("aa.aa")
    }
  }

  it should "drop invalid string" in {
    intercept[AssertionError] {
      Util.mapToStamp("112")
    }
    intercept[AssertionError] {
      Util.mapToStamp("112.112.112")
    }
  }

  it should "convert to stamp" in {
    val s = Util.mapToStamp("11211.11211")
    assert(s.sec == 11211)
    assert(s.nano == 11211)
  }

  it should "minus other stamp" in {
    val a = Stamp(0, 215) - Stamp(0, 210)
    assert(a.sec == 0 && a.nano == 5)
    val b = Stamp(1, 215) - Stamp(0, 210)
    assert(b.sec == 1 && b.nano == 5)
    val c = Stamp(1, 210) - Stamp(0, 215)
    assert(c.sec == 0 && c.nano == 999999995)
  }
}