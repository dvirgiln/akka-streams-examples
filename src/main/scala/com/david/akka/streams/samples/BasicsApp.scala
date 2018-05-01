package com.david.akka.streams.samples


import akka.NotUsed
import akka.actor.Cancellable
import akka.stream.javadsl.RunnableGraph
import akka.stream.scaladsl.{Flow, Keep, Sink, Source}

import scala.concurrent.{Future, Promise}


object BasicsApp extends App {

  val source2 = Source(1 to 4).map(_+ 1)

  source2.to(Sink.foreach(println))


  val broadcastSink: Sink[Int, NotUsed] = Flow[Int].alsoTo(Sink.foreach(println)).to(Sink.ignore)
  Source(1 to 6).to(broadcastSink)

}
