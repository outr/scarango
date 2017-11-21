name := "scarango"
organization in ThisBuild := "com.outr"
version in ThisBuild := "0.8.6"
scalaVersion in ThisBuild := "2.12.4"
crossScalaVersions in ThisBuild := List("2.12.4", "2.11.12")
scalacOptions in ThisBuild ++= Seq("-unchecked", "-deprecation")
resolvers in ThisBuild += Resolver.sonatypeRepo("releases")
resolvers in ThisBuild += Resolver.sonatypeRepo("snapshots")

val circeVersion = "0.8.0"
val reactifyVersion = "2.2.0"
val scalacticVersion = "3.0.3"
val scalaTestVersion = "3.0.3"
val scribeVersion = "1.4.5"
val youIVersion = "0.9.0-M2"
val diffsonVersion = "2.2.3"
val profigVersion = "1.1.3"

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
      "com.outr" %% "profig" % profigVersion,
      "io.youi" %% "youi-client" % youIVersion,
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