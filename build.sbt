import sbt.Keys.libraryDependencies

name := "scarango"
organization in ThisBuild := "com.outr"
version in ThisBuild := "0.6.0"
scalaVersion in ThisBuild := "2.12.2"
crossScalaVersions in ThisBuild := List("2.12.2", "2.11.11")
scalacOptions in ThisBuild ++= Seq("-unchecked", "-deprecation")
resolvers in ThisBuild += Resolver.sonatypeRepo("releases")
resolvers in ThisBuild += Resolver.sonatypeRepo("snapshots")

val circeVersion = "0.8.0"
val reactifyVersion = "2.0.3"
val scalacticVersion = "3.0.3"
val scalaTestVersion = "3.0.3"
val scribeVersion = "1.4.3"
val youIVersion = "0.4.1"
val diffsonVersion = "2.2.0"
val typeSafeConfigVersion = "1.3.1"

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
    description := "Core objects shared without driver-specific dependencies.",
    libraryDependencies += "org.scala-lang" % "scala-compiler" % scalaVersion.value
  )

lazy val coreJS = core.js
lazy val coreJVM = core.jvm

lazy val driver = project.in(file("driver"))
  .settings(
    name := "scarango-driver",
    fork := true,
    parallelExecution in Test := false,
    libraryDependencies ++= Seq(
      "com.outr" %% "scribe" % scribeVersion,
      "com.outr" %% "reactify" % reactifyVersion,
      "io.youi" %% "youi-client" % youIVersion,
      "com.typesafe" % "config" % typeSafeConfigVersion,
      "org.gnieh" %% "diffson-circe" % diffsonVersion,
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