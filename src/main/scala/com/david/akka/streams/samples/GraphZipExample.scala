package com.david.akka.streams.samples

import akka.actor.ActorSystem

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
object GraphZipExample extends App {
  import akka.stream._
  import akka.stream.scaladsl._
  implicit val system = ActorSystem("QuickStart")
  implicit val materializer = ActorMaterializer()

  /*
   * Basically what is getting done here:
   *
   * input0   -> Zip1.in0
   * input1   -> Zip1.in1
   *                      Zip1.out   -> Zip2.in0
   * input2                          -> Zip2.in1
   *                                            --> pickMaxOfThree.out
   */


  val pickMaxOfThree = GraphDSL.create() { implicit b ⇒
    import GraphDSL.Implicits._
    //Zip return a FainInShape2, one output and 2 inputs
    val zip1 = b.add(ZipWith[Int, Int, Int](math.max _))
    val zip2 = b.add(ZipWith[Int, Int, Int](math.max _))
    zip1.out ~> zip2.in0
    //
    UniformFanInShape(zip2.out, zip1.in0, zip1.in1, zip2.in1)
  }

  val resultSink = Sink.head[Int]

  val g = RunnableGraph.fromGraph(GraphDSL.create(resultSink) { implicit b ⇒ sink ⇒
    import GraphDSL.Implicits._

    // importing the partial graph will return its shape (inlets & outlets)
    val pm3 = b.add(pickMaxOfThree)

    Source.single(1) ~> pm3.in(0)
    Source.single(2) ~> pm3.in(1)
    Source.single(3) ~> pm3.in(2)
    pm3.out ~> sink.in
    ClosedShape
  })
  val values = g.run()
  values.onComplete{ v =>
    print(v.get)
    system.terminate()
  }

}
