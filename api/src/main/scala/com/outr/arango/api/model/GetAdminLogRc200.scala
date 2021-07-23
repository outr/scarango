package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class GetAdminLogRc200(lid: List[String],
                            level: Option[String] = None,
                            text: Option[String] = None,
                            timestamp: Option[List[String]] = None,
                            topic: Option[String] = None,
                            totalAmount: Option[Long] = None)

object GetAdminLogRc200 {
  implicit val rw: ReaderWriter[GetAdminLogRc200] = ccRW
}