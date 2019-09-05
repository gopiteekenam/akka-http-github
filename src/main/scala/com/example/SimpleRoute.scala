package com.example

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{StatusCodes, Uri}
import akka.stream.ActorMaterializer
import akka.http.scaladsl.server.Directives._

import scala.io.StdIn

object SimpleRoute extends App{
  implicit val system = ActorSystem("my-system")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  val route =
    path("hello"){
      get{
        redirect(Uri("http://google.com"),StatusCodes.PermanentRedirect)
      }

    }

  val bindingFuture = Http().bindAndHandle(route,"0.0.0.0",8282)

  println(s"Server online at http://loclahist:8080")
  StdIn.readLine
  bindingFuture.flatMap(_.unbind()).onComplete(_ => system.terminate())
}
class SimpleRoute {

}
