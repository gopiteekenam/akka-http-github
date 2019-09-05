package com.example

//#quick-start-server
import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, Uri}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import com.typesafe.config.ConfigFactory

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.{Failure, Success}
import concurrent.duration._


//#main-class
object QuickstartServer extends App with UserRoutes {

  // set up ActorSystem and other dependencies here
  //#main-class
  //#server-bootstrapping
  implicit val system: ActorSystem = ActorSystem("helloAkkaHttpServer")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContext = system.dispatcher

  //#server-bootstrapping

  val userRegistryActor: ActorRef = system.actorOf(UserRegistryActor.props, "userRegistryActor")

  //#main-class
  // from the UserRoutes trait
  lazy val routes: Route = userRoutes
  //#main-class
  val subUri = "/users/mojombo"

  val serviceUrl = {
    val config = ConfigFactory.load()
    config.getString("services.git-api.host")
  }

  val httpClient = Http().outgoingConnectionHttps(host = serviceUrl)
  val future: Future[User] =
    Source.single(HttpRequest(uri = Uri(subUri)))
    .via(httpClient)
    .mapAsync(1)(response => Unmarshal(response.entity).to[User])
    .runWith(Sink.head)

  future.onComplete {
    case Success(res) =>

      val start = System.currentTimeMillis()
      val result = Await.result(future, 15 seconds)
      val end = System.currentTimeMillis()
      println(res)
      system.terminate()
    case Failure(e) =>
      Console.err.println(s"Server could not start!")
      e.printStackTrace()
      system.terminate()
  }

  Await.result(system.whenTerminated, Duration.Inf)
  //#http-server
  //#main-class
}
//#main-class
//#quick-start-server
