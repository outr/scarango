import sbt.*

object Dependencies {
  object version {
    val fabric: String = "1.13.1"

    val profig: String = "3.4.12"
    
    val scalaPass: String = "1.2.8"

    val arangoDBJavaDriver: String = "7.5.1"

    val jackson: String = "4.2.0"
    
    val catsEffect: String = "3.5.2"
    
    val fs2: String = "3.9.3"
    
    val scribe: String = "3.13.0"

    val scalaMeta: String = "4.8.7"

    val spice: String = "0.5.0"

    val scalaTest: String = "3.2.17"
    
    val catsEffectTesting: String = "1.5.0"
  }
  val fabric: ModuleID = "org.typelevel" %% "fabric-io" % version.fabric
  val profig: ModuleID = "com.outr" %% "profig" % version.profig
  val scalaPass: ModuleID = "com.outr" %% "scalapass" % version.scalaPass
  val arangoDBJavaDriver: ModuleID = "com.arangodb" % "arangodb-java-driver" % version.arangoDBJavaDriver
  val jacksonDataformatVelocypack: ModuleID = "com.arangodb" % "jackson-dataformat-velocypack" % version.jackson
  val catsEffect: ModuleID = "org.typelevel" %% "cats-effect" % version.catsEffect
  val fs2: ModuleID = "co.fs2" %% "fs2-core" % version.fs2
  val scribe: ModuleID = "com.outr" %% "scribe" % version.scribe
  val scribeSlf4j: ModuleID = "com.outr" %% "scribe-slf4j2" % version.scribe
  val scalaMeta: ModuleID = "org.scalameta" %% "scalameta" % version.scalaMeta
  val spiceClient: ModuleID = "com.outr" %% "spice-client-jvm" % version.spice

  val scalaTest: ModuleID = "org.scalatest" %% "scalatest" % version.scalaTest % "test"
  val catsEffectTesting: ModuleID = "org.typelevel" %% "cats-effect-testing-scalatest" % version.catsEffectTesting % "test"
}