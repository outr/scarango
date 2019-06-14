import sbtcrossproject.CrossPlugin.autoImport.{crossProject, CrossType}

name := "scarango"
organization in ThisBuild := "com.outr"
version in ThisBuild := "2.0.0-SNAPSHOT"
scalaVersion in ThisBuild := "2.12.8"
crossScalaVersions in ThisBuild := List("2.12.8", "2.11.12")
scalacOptions in ThisBuild ++= Seq("-unchecked", "-deprecation")
resolvers in ThisBuild += Resolver.sonatypeRepo("releases")
resolvers in ThisBuild += Resolver.sonatypeRepo("snapshots")

val youiVersion = "0.11.6-SNAPSHOT"
val scalaTestVersion = "3.0.5"

lazy val root = project.in(file("."))
  .aggregate(api, coreJS, coreJVM, driver)
  .settings(
    publish := {},
    publishLocal := {}
  )

lazy val api = project.in(file("api"))
  .settings(
    name := "scarango-api",
    libraryDependencies ++= Seq(
      "org.scala-lang" % "scala-compiler" % scalaVersion.value,
      "io.youi" %% "youi-client" % youiVersion,
      "org.scalatest" %% "scalatest" % scalaTestVersion % "test"
    )
  )

lazy val core = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("core"))
  .settings(
    name := "scarango-core",
    libraryDependencies ++= Seq(
      "io.youi" %% "youi-core" % youiVersion
    )
  )

lazy val coreJS = core.js
lazy val coreJVM = core.jvm

lazy val driver = project.in(file("driver"))
  .settings(
    name := "scarango-driver",
    fork := true,
    testOptions in Test += Tests.Argument("-oD"),
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % scalaTestVersion % "test"
    )
  )
  .dependsOn(coreJVM, api)

lazy val plugin = project.in(file("plugin"))
  .settings(
    name := "scarango-plugin",
    sbtPlugin := true,
    crossSbtVersions := Vector("0.13.18", "1.2.8")
  )