enablePlugins(JavaAppPackaging)

name := "utility-ussd"
version := "1.0"
scalaVersion := "2.12.6"
lazy val AkkaVersion = "2.6.3"
lazy val AkkaHttpVersion = "10.1.11"
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % AkkaVersion,
  "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % AkkaVersion,
  "com.typesafe.akka" %% "akka-http-core" % AkkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % AkkaHttpVersion,
  "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-xml" % AkkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-testkit" % AkkaHttpVersion % "test",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
  "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
  "com.typesafe.akka" %% "akka-actor-testkit-typed" % AkkaVersion % Test,
  "com.mashape.unirest" % "unirest-java" % "1.4.9",
  "com.pauldijou" %% "jwt-core" % "4.2.0",
  "net.liftweb" %% "lift-json" % "3.4.1",
  "com.lihaoyi" %% "ujson" % "1.2.2",
  "com.lihaoyi" %% "requests" % "0.6.5",
  "com.lihaoyi" %% "upickle" % "1.2.2",
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "io.netty" % "netty-all" % "4.1.33.Final",
  "org.scalatest" %% "scalatest" % "3.1.0" % Test,
  "com.google.firebase" % "firebase-admin" % "6.14.0"
)
dockerExposedPorts ++= Seq(7500)
