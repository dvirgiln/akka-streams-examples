package com.david.akka.streams.samples

import akka.actor.ActorSystem

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
object SumNumbersApp extends App {
  import akka.stream._
  import akka.stream.scaladsl._
  implicit val system = ActorSystem("QuickStart")
  implicit val materializer = ActorMaterializer()
  val source = Source(1 to 10)
  val sink = Sink.fold[Int, Int](0)(_ + _)

  // connect the Source to the Sink, obtaining a RunnableGraph
  val runnable: RunnableGraph[Future[Int]] = source.toMat(sink)(Keep.right)

  // materialize the flow and get the value of the FoldSink
  val sum: Future[Int] = runnable.run()

  //Exactly the same

  // materialize the flow, getting the Sinks materialized value
  val sum2: Future[Int] = source.runWith(sink)

  Future.sequence(Seq(sum, sum2)).onComplete{ values =>
    values.get.foreach(println)
    system.terminate()
  }
}
