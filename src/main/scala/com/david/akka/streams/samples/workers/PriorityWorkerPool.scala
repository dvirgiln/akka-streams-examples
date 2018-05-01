package com.david.akka.streams.samples.workers

import akka.NotUsed

object PriorityWorkerPool {
  import akka.stream._
  import akka.stream.scaladsl._
  def apply[In, Out](
                      worker:      Flow[In, Out, Any],
                      workerCount: Int): Graph[PriorityWorkerPoolShape[In, Out], NotUsed] = {

    GraphDSL.create() { implicit b ⇒
      import GraphDSL.Implicits._

      val priorityMerge = b.add(MergePreferred[In](1))
      //Emits the message to the first worker available. Difference with broadcast:
      //https://stackoverflow.com/questions/40222075/difference-between-balance-and-broadcast-fan-out-in-akka-streams
      val balance = b.add(Balance[In](workerCount))
      val resultsMerge = b.add(Merge[Out](workerCount))

      // After merging priority and ordinary jobs, we feed them to the balancer
      priorityMerge ~> balance

      // Wire up each of the outputs of the balancer to a worker flow
      // then merge them back
      for (i ← 0 until workerCount)
        balance.out(i) ~> worker ~> resultsMerge.in(i)

      // We now expose the input ports of the priorityMerge and the output
      // of the resultsMerge as our PriorityWorkerPool ports
      // -- all neatly wrapped in our domain specific Shape
      PriorityWorkerPoolShape(
        jobsIn = priorityMerge.in(0),
        priorityJobsIn = priorityMerge.preferred,
        resultsOut = resultsMerge.out)
    }

  }

}