package com.david.akka.streams.samples

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Sink, Source}
import akka.stream.testkit.scaladsl.TestSink
import akka.testkit.TestProbe
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpec}
import org.scalatest.concurrent.ScalaFutures

import scala.concurrent.duration._
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
class BidiExampleSpec extends WordSpec with Matchers with ScalaFutures with BeforeAndAfterAll {
    implicit val system = ActorSystem("QuickStart")
    implicit val materializer = ActorMaterializer()


  "Bidirectional flow" should {
    "return the ping of the sources with a pong in the same order" in {
      val flow: Flow[Message,Message, NotUsed] = BidiExample.getBidiFlow

      //A nice Sink is Sink.seq, or Sink.collection... Check the default Sinks.
      Source((0 to 3).map(Ping)).via(flow).limit(20)
        .runWith(TestSink.probe[Message])
        .request(4)
        .expectNext(Pong(0))
        .expectNext(Pong(1))
        .expectNext(Pong(2))
        .expectNext(Pong(3))
        .expectComplete()
    }
  }
}
