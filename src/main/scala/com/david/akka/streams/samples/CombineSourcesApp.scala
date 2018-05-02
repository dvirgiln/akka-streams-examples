package com.david.akka.streams.samples

import akka.actor.ActorSystem

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object CombineSourcesApp extends App {

  import akka.stream._
  import akka.stream.scaladsl._
  implicit val system = ActorSystem("QuickStart")
  implicit val materializer = ActorMaterializer()
  val sourceOne = Source(List(1))
  val sourceTwo = Source(List(2))
  //It is doing the same as a Zip
  val merged = Source.combine(sourceOne, sourceTwo)(Merge(_))

  val mergedResult: Future[Int] = merged.runWith(Sink.fold(0)(_ + _))
  mergedResult.onComplete{ v =>
    print(v.get)
    system.terminate()}


  //You can combine as well Sinks
}
