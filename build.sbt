name := "DSLDemo"

version := "0.1"

scalaVersion := "2.12.6"

libraryDependencies += "com.typesafe.akka" %% "akka-stream" % "2.5.15"
libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.5.15"
libraryDependencies += "com.typesafe.akka" %% "akka-http" % "10.1.4"
libraryDependencies += "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.4"

libraryDependencies += "org.scalamock" %% "scalamock" % "4.1.0" % Test
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % Test
libraryDependencies += "com.typesafe.akka" %% "akka-testkit" % "2.5.15" % Test
