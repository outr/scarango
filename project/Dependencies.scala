import sbt._

object Dependencies {
  object version {
    val profig: String = "3.2.8"
    val arangoDBJavaDriver: String = "6.14.0"
    val jackson: String = "3.0.0"
    val catsEffect: String = "3.3.0"
    val fs2: String = "3.2.2"
    val scribe: String = "3.6.7"
    val scalaTest: String = "3.2.10"
    val catsEffectTesting: String = "1.4.0"
  }

  val profig: ModuleID = "com.outr" %% "profig" % version.profig
  val arangoDBJavaDriver: ModuleID = "com.arangodb" % "arangodb-java-driver" % version.arangoDBJavaDriver
  val jacksonDataformatVelocypack: ModuleID = "com.arangodb" % "jackson-dataformat-velocypack" % version.jackson
  val catsEffect: ModuleID = "org.typelevel" %% "cats-effect" % version.catsEffect
  val fs2: ModuleID = "co.fs2" %% "fs2-core" % version.fs2
  val scribeSlf4j: ModuleID = "com.outr" %% "scribe-slf4j" % version.scribe

  val scalaTest: ModuleID = "org.scalatest" %% "scalatest" % version.scalaTest % "test"
  val catsEffectTesting: ModuleID = "org.typelevel" %% "cats-effect-testing-scalatest" % version.catsEffectTesting % "test"
}