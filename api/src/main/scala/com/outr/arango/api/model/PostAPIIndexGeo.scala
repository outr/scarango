package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class PostAPIIndexGeo(`type`: String,
                           fields: Option[List[String]] = None,
                           geoJson: Option[String] = None)

object PostAPIIndexGeo {
  implicit val rw: ReaderWriter[PostAPIIndexGeo] = ccRW
}