package com.example

import akka.Done
import akka.actor.ActorSystem
import akka.kafka.ProducerSettings
import akka.kafka.scaladsl.Producer
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import com.typesafe.config.ConfigFactory
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer

import scala.concurrent.Future

object KafkaProducer extends App{
  implicit val system = ActorSystem("QuickStart")
  implicit val materializer = ActorMaterializer()

  val config = ConfigFactory.load.getConfig("akka.kafka.producer")

  val producerSettings =
    ProducerSettings(config, new StringSerializer, new StringSerializer)
      .withBootstrapServers("http://localhost:9091")


val producerSink: Future[Done] =
Source(1 to 10)
.map(_.toString)
.map(value => new ProducerRecord[String, String]("test", "Record:: " + value))
.runWith(Producer.plainSink(producerSettings))
println("************ Message produced ************")

}

class KafkaProducer {

}
