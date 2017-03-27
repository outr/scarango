name := "arangodb-scala"
organization := "com.outr"
version := "1.0.0"
scalaVersion := "2.12.1"
crossScalaVersions := List("2.12.1", "2.11.8")
sbtVersion := "0.13.13"
scalacOptions ++= Seq("-unchecked", "-deprecation")
resolvers += Resolver.sonatypeRepo("releases")
fork := true

val circeVersion = "0.7.0"

libraryDependencies ++= Seq(
	"com.outr" %% "scribe" % "1.4.1",
	"io.youi" %% "youi-client" % "0.2.4",
  "org.powerscala" %% "powerscala-io" % "2.0.5",
	"org.scalactic" %% "scalactic" % "3.0.1",
	"org.scalatest" %% "scalatest" % "3.0.1" % "test"
)

libraryDependencies ++= Seq(
	"io.circe" %% "circe-core",
	"io.circe" %% "circe-generic",
	"io.circe" %% "circe-parser"
).map(_ % circeVersion)