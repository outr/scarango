import sbt.Keys.libraryDependencies

name := "scarango"
organization in ThisBuild := "com.outr"
version in ThisBuild := "0.3.1-SNAPSHOT"
scalaVersion in ThisBuild := "2.12.2"
crossScalaVersions in ThisBuild := List("2.12.2", "2.11.11")
scalacOptions in ThisBuild ++= Seq("-unchecked", "-deprecation")
resolvers in ThisBuild += Resolver.sonatypeRepo("releases")

val circeVersion = "0.7.1"
val reactifyVersion = "1.5.3"
val scalacticVersion = "3.0.3"
val scalaTestVersion = "3.0.3"
val scribeVersion = "1.4.2"
val youIVersion = "0.3.1"

lazy val root = project.in(file("."))
  .aggregate(
    coreJS, coreJVM, driver
  )
  .settings(
    publish := {},
    publishLocal := {}
  )

lazy val core = crossProject.in(file("core"))
  .settings(
    name := "scarango-core",
    description := "Core objects shared without driver-specific dependencies."
  )

lazy val coreJS = core.js
lazy val coreJVM = core.jvm

lazy val driver = project.in(file("driver"))
  .settings(
    name := "scarango-driver",
    fork := true,
    libraryDependencies ++= Seq(
      "com.outr" %% "scribe" % scribeVersion,
      "com.outr" %% "reactify" % reactifyVersion,
      "io.youi" %% "youi-client" % youIVersion,
      "org.scalactic" %% "scalactic" % scalacticVersion,
      "org.scalatest" %% "scalatest" % scalaTestVersion % "test"
    ),
    libraryDependencies ++= Seq(
      "io.circe" %% "circe-core",
      "io.circe" %% "circe-generic",
      "io.circe" %% "circe-parser"
    ).map(_ % circeVersion)
  )
  .dependsOn(coreJVM)