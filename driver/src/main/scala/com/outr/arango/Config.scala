package com.outr.arango

import fabric.rw.{ReaderWriter, ccRW}
import io.youi.net.URL

case class Config(db: String,
                  url: URL,
                  authentication: Boolean,
                  credentials: Credentials)

object Config {
  implicit val rw: ReaderWriter[Config] = ccRW
}