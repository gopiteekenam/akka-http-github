package com.example


import java.util.Properties

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, Uri}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import com.fasterxml.jackson.databind.{DeserializationFeature, JsonNode, ObjectMapper}
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper
import com.typesafe.config.ConfigFactory
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Success


//#main-class
object KafkaJSONPush extends App with UserRoutes {

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

  val appconfig = ConfigFactory.load()

  val serviceUrl = {
    appconfig.getString("services.git-api.host")
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
      val end = System.currentTimeMillis()
      println(res)
      val config = ConfigFactory.load.getConfig("akka.kafka.producer")
      val props = new Properties()
      props.put("bootstrap.servers", appconfig.getString("akka.kafka.bootstrap.url"))
      props.put("client.id", "ScalaProducerExample")
      props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
      props.put("value.serializer", "org.apache.kafka.connect.json.JsonSerializer")

      val producer = new KafkaProducer[String, JsonNode](props)
      //create object mapper
      val mapper = new ObjectMapper with ScalaObjectMapper
      mapper.registerModule(DefaultScalaModule)
      mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

      //mapper Json object to string
      def toJson(value: Any): String = {
        mapper.writeValueAsString(value)
      }
      //send producer message
      val jsonstring =
        s"""{
           | "name": "Peter",
           | "mail": "Peter@pet.com"
           |}
         """.stripMargin

      val jsonNode: JsonNode = mapper.readTree(jsonstring)


      val rec = new ProducerRecord[String, JsonNode]("sourceK", jsonNode)
      producer.send(rec)

      producer.close()
      system.terminate()
  }
}
