package com.david.akka.streams.samples

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.testkit.scaladsl.{TestSink, TestSource}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpec}
import org.scalatest.concurrent.ScalaFutures
import scala.concurrent.Future
import scala.concurrent.duration._
import akka.pattern
class PublishSubscriberSpec  extends WordSpec with Matchers with ScalaFutures with BeforeAndAfterAll {
  implicit val system = ActorSystem("QuickStart")
  implicit val materializer = ActorMaterializer()
  import akka.stream._
  import akka.stream.scaladsl._
  import akka.stream.testkit._
  import akka.stream.testkit.scaladsl._
  import scala.concurrent.ExecutionContext.Implicits.global
  "Publish subscriber" should {
    "publish and consume elements and send exceptions." in {
      val flowUnderTest = Flow[Int].mapAsyncUnordered(2) { sleep ⇒
        pattern.after(10.millis * sleep, using = system.scheduler)(Future.successful(sleep))
      }

      val (pub, sub) = TestSource.probe[Int]
        .via(flowUnderTest)
        .toMat(TestSink.probe[Int])(Keep.both)
        .run()

      sub.request(n = 3)
      pub.sendNext(3)
      pub.sendNext(2)
      pub.sendNext(1)
      sub.expectNextUnordered(1, 2, 3)

      pub.sendError(new Exception("Power surge in the linear subroutine C-47!"))
      val ex = sub.expectError()
      assert(ex.getMessage.contains("C-47"))
    }
  }

}
