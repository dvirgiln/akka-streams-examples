package com.david.akka.streams.samples

package com.david.akka.streams.samples
import akka.actor.ActorSystem

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
object SourceShapeInOutApp extends App {
  import akka.stream._
  import akka.stream.scaladsl._
  implicit val system = ActorSystem("QuickStart")
  implicit val materializer = ActorMaterializer()
  val pairUpWithToString =
    Flow.fromGraph(GraphDSL.create() { implicit b ⇒
      import GraphDSL.Implicits._

      // prepare graph elements
      val broadcast = b.add(Broadcast[Int](2))
      val zip = b.add(Zip[Int, String]())

      // connect the graph
      broadcast.out(0).map(identity) ~> zip.in0
      broadcast.out(1).map(_.toString) ~> zip.in1

      // expose ports
      FlowShape(broadcast.in, zip.out)
    })



  val (_,firstPair) = pairUpWithToString.runWith(Source(List(1)), Sink.head)
  firstPair.onComplete{ v =>
    print(v.get)
    system.terminate()
  }
}
