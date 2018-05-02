package com.david.akka.streams.samples.workers

import akka.stream.FanInShape
import akka.stream.FanInShape.Init

case class PriorityWorkerPoolShape2[In, Out](_init: FanInShape.Init[Out])
  extends FanInShape[Out](_init) {
  protected override def construct(i: Init[Out]) = new PriorityWorkerPoolShape2(i)

  val jobsIn = newInlet[In]("jobsIn")
  val priorityJobsIn = newInlet[In]("priorityJobsIn")
  // Outlet[Out] with name "out" is automatically created
}