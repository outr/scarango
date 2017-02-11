name := "arangodb-scala"
organization := "com.outr"
version := "1.0.0"
scalaVersion := "2.12.1"
crossScalaVersions := List("2.12.1", "2.11.8")
sbtVersion := "0.13.13"
scalacOptions ++= Seq("-unchecked", "-deprecation")
resolvers += Resolver.sonatypeRepo("releases")

libraryDependencies ++= Seq(
	"com.outr" %% "scribe" % "1.3.2",
	"com.eed3si9n" %% "gigahorse-asynchttpclient" % "0.2.0",
  "com.lihaoyi" %% "upickle" % "0.4.4",
	"org.scalactic" %% "scalactic" % "3.0.1",
	"org.scalatest" %% "scalatest" % "3.0.1" % "test"
)