import sbtcrossproject.CrossPlugin.autoImport.{crossProject, CrossType}
import Tests._

name := "scarango"
ThisBuild / organization := "com.outr"
ThisBuild / version := "3.0.0-SNAPSHOT"
ThisBuild / scalaVersion := "2.13.6"
ThisBuild / crossScalaVersions := List("2.13.6", "2.12.12")
ThisBuild / scalacOptions ++= Seq("-unchecked", "-deprecation")
ThisBuild / resolvers += Resolver.sonatypeRepo("releases")
ThisBuild / resolvers += Resolver.sonatypeRepo("snapshots")

ThisBuild / publishTo := sonatypePublishToBundle.value
ThisBuild / sonatypeProfileName := "com.outr"
ThisBuild / publishMavenStyle := true
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
  Developer(id="darkfrog", name="Matt Hicks", email="matt@matthicks.com", url=url("http://matthicks.com"))
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
      dep.profig
    )
  )

lazy val coreJS = core.js
lazy val coreJVM = core.jvm

lazy val driver = project.in(file("driver"))
  .settings(
    name := "scarango-driver",
    fork := true,
    Test / testGrouping := groupByName((Test / definedTests).value),
    Test / testOptions += Tests.Argument("-oD"),
    Test / parallelExecution := false,
    libraryDependencies ++= Seq(
      dep.arangoDBJavaDriver,
      dep.jacksonDataformatVelocypack,
      dep.catsEffect,
      dep.fs2,
      dep.scalaTest
    )
  )
  .dependsOn(coreJVM)

//lazy val monitored = project.in(file("monitored"))
//  .settings(
//    name := "scarango-monitored",
//    fork := true,
//    Test / testGrouping := groupByName((Test / definedTests).value),
//    Test / testOptions += Tests.Argument("-oD"),
//    Test / parallelExecution := false,
//    libraryDependencies ++= Seq(
//      "org.scalatest" %% "scalatest" % scalaTestVersion % "test"
//    )
//  )
//  .dependsOn(driver)
//
//lazy val plugin = project.in(file("plugin"))
//  .settings(
//    name := "scarango-plugin",
//    sbtPlugin := true,
//    scalaVersion := "2.12.13",
//    crossSbtVersions := Vector("1.5.2")
//  )