import sbtcrossproject.CrossPlugin.autoImport.{crossProject, CrossType}
import Tests._

// Scala versions
val scala213 = "2.13.5"
val scala212 = "2.12.13"
val scala3 = "3.0.0-RC1"
val scala2 = List(scala213, scala212)
val allScalaVersions = scala3 :: scala2
val scalaJVMVersions = allScalaVersions
val scalaJSVersions = allScalaVersions

// Variables
val org: String = "com.outr"
val projectName: String = "scarango"
val githubOrg: String = "outr"
val email: String = "matt@matthicks.com"
val developerId: String = "darkfrog"
val developerName: String = "Matt Hicks"
val developerURL: String = "http://matthicks.com"

name := projectName
ThisBuild / organization := org
ThisBuild / version := "2.4.3-SNAPSHOT"
ThisBuild / scalaVersion := scala213
ThisBuild / scalacOptions ++= Seq("-unchecked", "-deprecation")
ThisBuild / javacOptions ++= Seq("-source", "1.8", "-target", "1.8")

ThisBuild / resolvers += "yahoo-bintray" at "https://yahoo.bintray.com/maven"

ThisBuild / publishTo := sonatypePublishTo.value
ThisBuild / sonatypeProfileName := org
ThisBuild / licenses := Seq("MIT" -> url(s"https://github.com/$githubOrg/$projectName/blob/master/LICENSE"))
ThisBuild / sonatypeProjectHosting := Some(xerial.sbt.Sonatype.GitHubHosting(githubOrg, projectName, email))
ThisBuild / homepage := Some(url(s"https://github.com/$githubOrg/$projectName"))
ThisBuild / scmInfo := Some(
  ScmInfo(
    url(s"https://github.com/$githubOrg/$projectName"),
    s"scm:git@github.com:$githubOrg/$projectName.git"
  )
)
ThisBuild / developers := List(
  Developer(id=developerId, name=developerName, email=email, url=url(developerURL))
)

val youiVersion = "0.14.0"
val profigVersion = "3.2.1"
val scalaTestVersion = "3.2.3"

def groupByName(tests: Seq[TestDefinition]): Seq[Group] = {
  tests.groupBy(_.name).map {
    case (n, t) =>
      val options = ForkOptions()
      Group(n, t, SubProcess(options))
  }.toSeq
}

lazy val root = project.in(file("."))
  .aggregate(api, coreJS, coreJVM, driver, monitored)
  .settings(
    name := projectName,
    publish := {},
    publishLocal := {}
  )

lazy val api = project.in(file("api"))
  .settings(
    name := s"$projectName-api",
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
    name := s"$projectName-core",
    libraryDependencies ++= Seq(
      "io.youi" %% "youi-core" % youiVersion
    )
  )

lazy val coreJS = core.js
lazy val coreJVM = core.jvm

lazy val driver = project.in(file("driver"))
  .settings(
    name := s"$projectName-driver",
    fork := true,
    libraryDependencies ++= Seq(
      "com.outr" %% "profig" % profigVersion,
      "org.scalatest" %% "scalatest" % scalaTestVersion % "test"
    )
  )
  .dependsOn(coreJVM, api)

lazy val monitored = project.in(file("monitored"))
  .settings(
    name := s"$projectName-monitored",
    fork := true,
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % scalaTestVersion % "test"
    )
  )
  .dependsOn(driver)

lazy val plugin = project.in(file("plugin"))
  .settings(
    name := s"$projectName-plugin",
    sbtPlugin := true,
    crossSbtVersions := Vector("0.13.18", "1.5.0")
  )
