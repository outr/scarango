package com.outr.arango

import io.youi.net.URL

case class Config(db: String,
                  url: URL,
                  authentication: Boolean,
                  credentials: Credentials)