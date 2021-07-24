package com.outr.arango

import fabric.rw.{ReaderWriter, ccRW}
import io.youi.net.URL

case class Config(db: String = "_system",
                  url: URL = URL("http://localhost:8529"),
                  authentication: Boolean = true,
                  credentials: Credentials = Credentials("root", "root"))

object Config {
  implicit val rw: ReaderWriter[Config] = ccRW
}