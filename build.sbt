import sbtcrossproject.CrossPlugin.autoImport.{crossProject, CrossType}
import Tests._

name := "scarango"
organization in ThisBuild := "com.outr"
version in ThisBuild := "2.3.5"
scalaVersion in ThisBuild := "2.13.1"
crossScalaVersions in ThisBuild := List("2.13.1", "2.12.11")
scalacOptions in ThisBuild ++= Seq("-unchecked", "-deprecation")
resolvers in ThisBuild += Resolver.sonatypeRepo("releases")
resolvers in ThisBuild += Resolver.sonatypeRepo("snapshots")

publishTo in ThisBuild := sonatypePublishToBundle.value
sonatypeProfileName in ThisBuild := "com.outr"
publishMavenStyle in ThisBuild := true
licenses in ThisBuild := Seq("MIT" -> url("https://github.com/outr/scarango/blob/master/LICENSE"))
sonatypeProjectHosting in ThisBuild := Some(xerial.sbt.Sonatype.GitHubHosting("outr", "scarango", "matt@outr.com"))
homepage in ThisBuild := Some(url("https://github.com/outr/scarango"))
scmInfo in ThisBuild := Some(
  ScmInfo(
    url("https://github.com/outr/scarango"),
    "scm:git@github.com:outr/scarango.git"
  )
)
developers in ThisBuild := List(
  Developer(id="darkfrog", name="Matt Hicks", email="matt@matthicks.com", url=url("http://matthicks.com"))
)

val youiVersion = "0.13.0"
val scalaTestVersion = "3.2.0-M4"

def groupByName(tests: Seq[TestDefinition]): Seq[Group] = {
  tests.groupBy(_.name).map {
    case (n, t) =>
      val options = ForkOptions()
      Group(n, t, SubProcess(options))
  }.toSeq
}

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
    testGrouping in Test := groupByName((definedTests in Test).value),
    testOptions in Test += Tests.Argument("-oD"),
    parallelExecution in Test := false,
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % scalaTestVersion % "test"
    )
  )
  .dependsOn(coreJVM, api)

lazy val plugin = project.in(file("plugin"))
  .settings(
    name := "scarango-plugin",
    sbtPlugin := true,
    crossSbtVersions := Vector("0.13.18", "1.3.8")
  )