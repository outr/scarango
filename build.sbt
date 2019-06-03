import sbtcrossproject.CrossPlugin.autoImport.crossProject

name := "scarango"
organization in ThisBuild := "com.outr"
version in ThisBuild := "2.0.0-SNAPSHOT"
scalaVersion in ThisBuild := "2.12.8"
crossScalaVersions in ThisBuild := List("2.12.8")
scalacOptions in ThisBuild ++= Seq("-unchecked", "-deprecation")
resolvers in ThisBuild += Resolver.sonatypeRepo("releases")
resolvers in ThisBuild += Resolver.sonatypeRepo("snapshots")

val youiVersion = "0.11.2"
val scalaTestVersion = "3.0.5"

lazy val root = project.in(file("."))
  .aggregate(core)
  .settings(
    publish := {},
    publishLocal := {}
  )

lazy val core = project.in(file("core"))
  .settings(
    name := "scarango-core",
    description := "Core objects shared without driver-specific dependencies.",
    libraryDependencies ++= Seq(
      "org.scala-lang" % "scala-compiler" % scalaVersion.value,
      "io.youi" %% "youi-client" % youiVersion,
      "org.scalatest" %% "scalatest" % scalaTestVersion % "test"
    )
  )