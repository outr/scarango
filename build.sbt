import sbtcrossproject.CrossPlugin.autoImport.{crossProject, CrossType}
import Tests._

val scala213 = "2.13.10"
val scala3 = "3.2.2"

name := "scarango"
ThisBuild / organization := "com.outr"
ThisBuild / version := "3.10.0-SNAPSHOT"
ThisBuild / scalaVersion := scala213
ThisBuild / crossScalaVersions := List(scala3, scala213)
ThisBuild / scalacOptions ++= Seq("-unchecked", "-deprecation")

ThisBuild / sonatypeCredentialHost := "s01.oss.sonatype.org"
ThisBuild / sonatypeRepository := "https://s01.oss.sonatype.org/service/local"
ThisBuild / publishTo := sonatypePublishToBundle.value
ThisBuild / sonatypeProfileName := "com.outr"
ThisBuild / licenses := Seq("MIT" -> url("https://github.com/outr/scarango/blob/master/LICENSE"))
ThisBuild / sonatypeProjectHosting := Some(xerial.sbt.Sonatype.GitHubHosting("outr", "scarango", "matt@outr.com"))
ThisBuild / homepage := Some(url("https://github.com/outr/scarango"))
ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/outr/scarango"),
    "scm:git@github.com:outr/scarango.git"
  )
)
ThisBuild / developers := List(
  Developer(id="darkfrog", name="Matt Hicks", email="matt@matthicks.com", url=url("https://matthicks.com"))
)

def dep: Dependencies.type = Dependencies

def groupByName(tests: Seq[TestDefinition]): Seq[Group] = {
  tests.groupBy(_.name).map {
    case (n, t) =>
      val options = ForkOptions()
      Group(n, t, SubProcess(options))
  }.toSeq
}

lazy val root = project.in(file("."))
  .aggregate(coreJS, coreJVM, driver) //, monitored)
  .settings(
    publish := {},
    publishLocal := {}
  )

lazy val core = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("core"))
  .settings(
    name := "scarango-core",
    libraryDependencies ++= Seq(
      dep.profig,
      dep.fabric,
      dep.scalaPass,
      dep.scalaTest
    )
  )

lazy val coreJS = core.js
lazy val coreJVM = core.jvm

lazy val driver = project.in(file("driver"))
  .settings(
    name := "scarango-driver",
    fork := true,
    Test / testGrouping := groupByName((Test / definedTests).value),
    Test / testOptions += Tests.Argument("-oF"),
    Test / parallelExecution := false,
    libraryDependencies ++= Seq(
      dep.arangoDBJavaDriver,
      dep.jacksonDataformatVelocypack,
      dep.catsEffect,
      dep.fs2,
      dep.scribeSlf4j,
      dep.scalaTest,
      dep.catsEffectTesting
    )
  )
  .dependsOn(coreJVM)

lazy val docs = project
  .in(file("documentation"))
  .dependsOn(driver)
  .enablePlugins(MdocPlugin)
  .settings(
    mdocVariables := Map(
      "VERSION" -> version.value
    ),
    mdocOut := file(".")
  )