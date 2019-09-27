package com.example

import java.util.{Properties, UUID}

import org.apache.avro.Schema
import org.apache.avro.Schema.Parser
import org.apache.avro.generic.{GenericData, GenericRecord}
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}

import scala.io.Source


class KafkaProducerExample{

}

object KafkaProducerExample extends App{

//  private[this] val logger = Logger(getClass.getSimpleName)

  val topic = "sample"

  private val props = new Properties()


  props.put("bootstrap.servers", "localhost:9091")
  props.put("message.send.max.retries", "5")
  props.put("request.required.acks", "-1")
  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("serializer.class", "kafka.serializer.DefaultEncoder")
  props.put("value.serializer", "io.confluent.kafka.serializers.KafkaAvroSerializer")
  props.put("client.id", UUID.randomUUID().toString())
  props.put("schema.registry.url", "http://127.0.0.1:8081")



  val schema: Schema = new Parser().parse(Source.fromURL(getClass.getResource("/schema.avsc")).mkString)
  System.out.println(s"serialize user=[$schema]")
  val genericUser: GenericRecord = new GenericData.Record(schema)
  genericUser.put("id", 1)
  genericUser.put("name", "johnsmith")
  genericUser.put("email", "johnsmith@gmail.com")


  //Serialize generic record into byte array

  val producer = new KafkaProducer[String, GenericRecord](props)

/*  val writer = new SpecificDatumWriter[GenericRecord](schema)
  System.out.println("writer is "+writer)
  val out = new ByteArrayOutputStream()
  val encoder: BinaryEncoder = EncoderFactory.get().binaryEncoder(out, null)
  writer.write(genericUser, encoder)
  encoder.flush()*/


//  val serializedBytes: Array[Byte] = out.toByteArray()
//  System.out.println(s"serialize user=[${Base64.getEncoder().encodeToString(serializedBytes)}]")
//  val queueMessage = new KeyedMessage[String, Array[Byte]](topic, serializedBytes)
  val data = new ProducerRecord[String, GenericRecord](topic, genericUser)
  producer.send(data)

  System.out.println("sent message")
  producer.close()
//  out.close()
}