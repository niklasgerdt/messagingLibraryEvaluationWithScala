package analysis

import java.io.File
import grizzled.slf4j.Logging

object AnalyzeSimulatorSpeed extends App with StatFunctions with Logging {
  val filename = Const.filepath + args(0)
  val file = new File(filename)
  assert(file.exists())
  info("Analyzing simulator speed. Using file : " + file.getAbsolutePath)
  val sample = Util.mapFileToRouteInfoList(file)

  val none: Option[RouteInfo] = None
  val agg = sample.foldLeft((none, 0L, 0L))(avef(_, _))
  val ave = agg._2 / (agg._3 - 1)
  val max = sample.foldLeft((none, 0L))(maxf(_, _))._2
  val min = sample.foldLeft((none, Const.maxNanos))(minf(_, _))._2
  val aggStd = sample.foldLeft((none, 0L, ave))(stdf(_, _))
  val std = Math.sqrt(aggStd._2 / (agg._3 - 1))

  info("Simulator average delay is: " + ave)
  info("Simulator maximum delay is: " + max)
  info("Simulator minimum delay is: " + min)
  info("Simulator variance is: " + std)
}

object AnalyzeRoutingSpeed extends App with StatFunctions with Logging {
  val srcFilename = Const.filepath + args(0)
  val dstFilename = Const.filepath + args(1)
  val srcfile = new File(srcFilename)
  val dstfile = new File(dstFilename)
  assert(srcfile.exists())
  assert(dstfile.exists())
  info("Analyzing simulator speed. Using files : " + srcfile.getAbsolutePath + " and " + dstfile.getAbsolutePath)

  val srcsample = Util.mapFileToRouteInfoList(srcfile)
  val dstsample = Util.mapFileToRouteInfoList(dstfile)

  val none: Option[RouteInfo] = None
  val agg = dstsample.foldLeft((0L, 0L))(aveRoutingf(_, _))
  val ave = agg._1 / (agg._2 - 1)
  val max = dstsample.foldLeft(0L)(maxRoutingf(_, _))
  val min = dstsample.foldLeft(Const.maxNanos)(minRoutingf(_, _))
  val aggStd = dstsample.foldLeft((0L, ave))(stdRoutingf(_, _))
  val std = Math.sqrt(aggStd._2 / (agg._2 - 1))

  info("Average routing delay is: " + ave)
  info("Routing maximum delay is: " + max)
  info("Routing minimum delay is: " + min)
  info("Routing variance is: " + std)

  //  val srcEvents = Util.mapFileToRouteInfoList(srcfile).map(_.id)
  //  val dstEvents = Util.mapFileToLines(dstfile).map(Util.mapToRouteInfo(_)).map(_.id)
  //  info("SRC EVENTS: " + srcEvents.size)
  //  info("DST EVENTS: " + dstEvents.size)
  //  val eventAggregate = srcEvents ++ dstEvents
  //  val distinctedEvents = eventAggregate.distinct.size
  //
  //  val droppedEvents = srcEvents.foldLeft(0)((a, b) => {
  //    val e = srcEvents.find(_ == b)
  //    if (e.isDefined)
  //      a
  //    else
  //      a + 1
  //  }
  //  )
  //  info("Events dropped: " + droppedEvents)

}
