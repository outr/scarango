import sbt._

object Dependencies {
  object version {
    val fabric: String = "1.3.0"
    val profig: String = "3.4.0"
    val scalaPass: String = "1.2.1"
    val arangoDBJavaDriver: String = "6.18.0"
    val jackson: String = "3.0.1"
    val catsEffect: String = "3.3.14"
    val fs2: String = "3.2.11"
    val scribe: String = "3.10.3"

    val scalaTest: String = "3.2.13"
    val catsEffectTesting: String = "1.4.0"
  }

  val fabric: ModuleID = "com.outr" %% "fabric-parse" % version.fabric
  val profig: ModuleID = "com.outr" %% "profig" % version.profig
  val scalaPass: ModuleID = "com.outr" %% "scalapass" % version.scalaPass
  val arangoDBJavaDriver: ModuleID = "com.arangodb" % "arangodb-java-driver" % version.arangoDBJavaDriver
  val jacksonDataformatVelocypack: ModuleID = "com.arangodb" % "jackson-dataformat-velocypack" % version.jackson
  val catsEffect: ModuleID = "org.typelevel" %% "cats-effect" % version.catsEffect
  val fs2: ModuleID = "co.fs2" %% "fs2-core" % version.fs2
  val scribe: ModuleID = "com.outr" %% "scribe" % version.scribe

  val scalaTest: ModuleID = "org.scalatest" %% "scalatest" % version.scalaTest % "test"
  val catsEffectTesting: ModuleID = "org.typelevel" %% "cats-effect-testing-scalatest" % version.catsEffectTesting % "test"
}