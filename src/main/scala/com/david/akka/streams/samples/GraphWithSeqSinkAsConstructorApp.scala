package com.david.akka.streams.samples

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.david.akka.streams.samples.SumNumbersApp.{sum, sum2, system}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object GraphWithSeqSinksAsConstructorApp extends App {
  import akka.stream._
  import akka.stream.scaladsl._
  implicit val system = ActorSystem("QuickStart")
  implicit val materializer = ActorMaterializer()

  val topHeadSink = Sink.head[Int]
  val bottomHeadSink = Sink.head[Int]
  val sharedDoubler = Flow[Int].map(_ * 2)

  val g= RunnableGraph.fromGraph(GraphDSL.create(topHeadSink, bottomHeadSink)((_, _)) { implicit builder =>
    (topHS, bottomHS) =>
      import GraphDSL.Implicits._
      val broadcast = builder.add(Broadcast[Int](2))
      Source.single(1) ~> broadcast.in

      broadcast ~> sharedDoubler ~> topHS.in
      broadcast ~> sharedDoubler ~> bottomHS.in
      ClosedShape
  })
  val values =g.run()
  Future.sequence(Seq(values._1, values._2)).onComplete{ values =>
    values.get.foreach(println)
    system.terminate()
  }
}
