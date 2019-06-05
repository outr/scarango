package com.outr.arango

import io.youi.net.URL

case class Config(db: String = "_system",
                  url: URL = URL("http://localhost:8529"),
                  authentication: Boolean = true,
                  credentials: Credentials = Credentials())