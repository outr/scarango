package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class PostAPIGharialCreateOpts(numberOfShards: Option[Int] = None,
                                    replicationFactor: Option[Int] = None,
                                    smartGraphAttribute: Option[String] = None)

object PostAPIGharialCreateOpts {
  implicit val rw: ReaderWriter[PostAPIGharialCreateOpts] = ccRW
}