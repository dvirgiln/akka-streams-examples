package com.david.akka.streams.samples
import akka.actor.ActorSystem

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
object SourceShapeGraphBasic extends App {
  import akka.stream._
  import akka.stream.scaladsl._
  implicit val system = ActorSystem("QuickStart")
  implicit val materializer = ActorMaterializer()
  val pairs = Source.fromGraph(GraphDSL.create() { implicit b ⇒
    import GraphDSL.Implicits._
    //Interesting this zip. the output is a Tuple[Int, Int]
    // prepare graph elements
    val zip = b.add(Zip[Int, Int]())
    // Lovely this, Using Iteratior.fp
    def ints = Source.fromIterator(() ⇒ Iterator.from(1))

    // connect the graph
    ints.filter(_ % 2 != 0) ~> zip.in0
    ints.filter(_ % 2 == 0) ~> zip.in1

    // expose port
    SourceShape(zip.out)
  })

  val firstPair: Future[(Int, Int)] = pairs.runWith(Sink.head)
  firstPair.onComplete{ v =>
    print(v.get)
    system.terminate()
  }
}
