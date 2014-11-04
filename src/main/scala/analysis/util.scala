package analysis

import java.io.File
import scala.io.Source

case class RouteInfo(src: String, dst: String, id: Long, prepub: Stamp, postpub: Stamp, routed: Stamp)

case class Stamp(sec: Long, nano: Long) {
  val SECL = 1000000000L

  def -(t: Stamp): Stamp = {
    val n = nano - t.nano
    val s = sec - t.sec
    if (n < 0L)
      Stamp(s - 1, SECL + n)
    else
      Stamp(s, n)
  }
}

object Const {
  val filepath = "/media/devel/repos/messaginglibraryevaluation/logs/"
  val maxNanos = 999999999L
  val drop = 1000000
  val take = 1000000
}

object Util {
  val reg = "\\d{1,9}".r
  val stampReg = "\\d{1,9}".r

  def isNum(str: String) = reg.findFirstIn(str).isDefined

  def mapToRouteInfo(str: String): RouteInfo = {
    val tokens = str.split(";")
    assert(tokens.length == 6)
    assert(Util.isNum(tokens(2)))
    val pre = mapToStamp(tokens(3))
    val post = mapToStamp(tokens(4))
    val routed = mapToStamp(tokens(5))
    RouteInfo(tokens(0), tokens(1), tokens(2).toLong, pre, post, routed)
  }

  def mapToStamp(str: String): Stamp = {
    val tokens = str.split("\\.")
    assert(tokens.length == 2)
    val sec = stampReg.findFirstIn(tokens(0))
    val nano = stampReg.findFirstIn(tokens(1))
    assert(sec.isDefined && nano.isDefined)
    val s = sec.get.toLong
    val n = nano.get.toLong
    Stamp(s, n)
  }

  def mapFileToRouteInfoList(file: File) =
    mapFileToLines(file).
      drop(Const.drop).
      take(Const.take).
      map(Util.mapToRouteInfo(_)).
      toList

  def mapFileToLines(file: File) =
    Source.fromFile(file).
      getLines()
}

trait StatFunctions {

  def avef(a: (Option[RouteInfo], Long, Long), cur: RouteInfo): (Option[RouteInfo], Long, Long) = {
    if (a._1.isEmpty)
      (Some(cur), 0, 0)
    else {
      val prev = a._1.get
      val elapsed = cur.prepub - prev.postpub
      (Some(cur), a._2 + elapsed.nano, a._3 + 1)
    }
  }

  def maxf(a: (Option[RouteInfo], Long), cur: RouteInfo): (Option[RouteInfo], Long) = {
    if (a._1.isEmpty)
      (Some(cur), 0)
    else {
      val prev = a._1.get
      val elapsed = cur.prepub - prev.postpub
      if (elapsed.nano > a._2)
        (Some(cur), elapsed.nano)
      else
        (Some(cur), a._2)
    }
  }

  def minf(a: (Option[RouteInfo], Long), cur: RouteInfo): (Option[RouteInfo], Long) = {
    if (a._1.isEmpty)
      (Some(cur), Const.maxNanos)
    else {
      val prev = a._1.get
      val elapsed = cur.prepub - prev.postpub
      if (elapsed.nano < a._2)
        (Some(cur), elapsed.nano)
      else
        (Some(cur), a._2)
    }
  }

  def stdf(a: (Option[RouteInfo], Long, Long), cur: RouteInfo): (Option[RouteInfo], Long, Long) = {
    if (a._1.isEmpty)
      (Some(cur), 0, a._3)
    else {
      val prev = a._1.get
      val elapsed = cur.prepub - prev.postpub
      val s = elapsed.nano - a._3
      val ss = s * s
      assert((a._2 + ss) >= a._2)
      (Some(cur), a._2 + ss, a._3)
    }
  }

  def aveRoutingf(a: (Long, Long), cur: RouteInfo): (Long, Long) = {
    val elapsed = cur.routed - cur.prepub
    (a._1 + elapsed.nano, a._2 + 1)
  }

  def maxRoutingf(a: Long, cur: RouteInfo): Long = {
    val elapsed = cur.routed - cur.prepub
    if (elapsed.nano > a)
      elapsed.nano
    else
      a
  }

  def minRoutingf(a: Long, cur: RouteInfo): Long = {
    val elapsed = cur.routed - cur.prepub
    if (elapsed.nano < a)
      elapsed.nano
    else
      a
  }

  def stdRoutingf(a: (Long, Long), cur: RouteInfo): (Long, Long) = {
    val elapsed = cur.routed - cur.prepub
    val s = elapsed.nano - a._2
    val ss = s * s
    assert(ss + a._1 > a._1)
    (a._1 + ss, a._2)
  }
}
