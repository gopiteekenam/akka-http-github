lazy val akkaHttpVersion = "10.1.9"
lazy val akkaVersion    = "2.5.25"
lazy val scalaTestVersion = "3.0.5"
lazy val kafkaVersion = "0.10.0.1"

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization    := "com.example",
      scalaVersion    := "2.11.8"
    )),
    name := "Akka-HTTP",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-http"            % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-xml"        % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-stream"          % akkaVersion,
      "com.typesafe.akka" %% "akka-slf4j"           % akkaVersion,
      "com.typesafe.akka" %% "akka-agent"           % akkaVersion,

      "com.typesafe.akka" %% "akka-http-testkit"    % akkaHttpVersion % Test,
      "com.typesafe.akka" %% "akka-testkit"         % akkaVersion     % Test,
      "com.typesafe.akka" %% "akka-stream-testkit"  % akkaVersion     % Test,
      "org.scalatest"     %% "scalatest"            % "3.0.5"         % Test,
      "com.typesafe.akka" %% "akka-stream-kafka" % "1.0-RC1",
      "io.kamon"          %% "kamon-logback"        % "1.0.6",
      "com.sksamuel.avro4s" %% "avro4s-core" % "1.6.2",
      "org.apache.kafka" %% "kafka" % kafkaVersion,
      "io.confluent" % "kafka-avro-serializer" % "3.0.1",
      "org.apache.kafka" % "connect-json" % "0.9.0.0",
      "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.10.0"

    )
    
    
  )
  resolvers ++= Seq(
  Resolver.sonatypeRepo("public"),
  "Confluent Maven Repo" at "http://packages.confluent.io/maven/"
)
