package com.david.akka.streams.samples

import java.nio.file.Paths

import akka.NotUsed
import akka.actor.ActorSystem
import akka.util.ByteString

import scala.concurrent._
import scala.concurrent.duration._
object FactorialApp extends App {
  println("beginning")

  import akka.stream._
  import akka.stream.scaladsl._
  implicit val system = ActorSystem("QuickStart")
  implicit val materializer = ActorMaterializer()
  val source: Source[Int, NotUsed] = Source(1 to 100)
  source.runForeach(i ⇒ println(i))(materializer)

  val factorials = source.scan(BigInt(1))((acc, next) ⇒ acc * next)

  val result: Future[IOResult] =
    factorials
      .map(num ⇒ ByteString(s"$num\n"))
      .runWith(FileIO.toPath(Paths.get("src/main/resources/factorials.txt")))
  println("end")

  val anotherResult =factorials
    .zipWith(Source(0 to 100))((num, idx) ⇒ s"$idx! = $num")
    .throttle(1, 1.second,5, ThrottleMode.Shaping)
    .runForeach(println)

  implicit val ec = system.dispatcher
  Future.sequence(Seq(result, anotherResult)).onComplete(_ ⇒ system.terminate())
}
