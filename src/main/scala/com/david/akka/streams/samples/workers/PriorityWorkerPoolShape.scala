package com.david.akka.streams.samples.workers

// A shape represents the input and output ports of a reusable
// processing module
import akka.stream._
import akka.stream.scaladsl._

import scala.collection.immutable.Seq

case class PriorityWorkerPoolShape[In, Out](
                                             jobsIn:         Inlet[In],
                                             priorityJobsIn: Inlet[In],
                                             resultsOut:     Outlet[Out]) extends Shape {

  // It is important to provide the list of all input and output
  // ports with a stable order. Duplicates are not allowed.
  override val inlets: Seq[Inlet[_]] =
  jobsIn :: priorityJobsIn :: Nil
  override val outlets: Seq[Outlet[_]] =
    resultsOut :: Nil

  // A Shape must be able to create a copy of itself. Basically
  // it means a new instance with copies of the ports
  override def deepCopy() = PriorityWorkerPoolShape(
    jobsIn.carbonCopy(),
    priorityJobsIn.carbonCopy(),
    resultsOut.carbonCopy())

}