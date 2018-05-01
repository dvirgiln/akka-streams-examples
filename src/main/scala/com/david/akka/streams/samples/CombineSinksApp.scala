package com.david.akka.streams.samples

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future



object CombineSinksApp extends App {
  import MyActor._
  import akka.stream._
  import akka.stream.scaladsl._
  implicit val system = ActorSystem("QuickStart")
  implicit val materializer = ActorMaterializer()

  val actorRef: ActorRef = system.actorOf(MyActor.props, "userRegistryActor")

  val sendRmotely = Sink.actorRef(actorRef, SendMessage("Done"))
  val localProcessing = Sink.foreach[Int](println)

  val sink = Sink.combine(sendRmotely, localProcessing)(Broadcast[Int](_))

  val values = Source(List(0, 1, 2)).runWith(sink)
}


object MyActor{
  def props: Props = Props[MyActor]
  final case class SendMessage(message: String)
}
class MyActor extends Actor with ActorLogging {
  import MyActor._
  override def receive: Receive = initialReceive

  def initialReceive: Receive = {
    case SendMessage(message) => println(s"Actor received a $message")
  }
}
