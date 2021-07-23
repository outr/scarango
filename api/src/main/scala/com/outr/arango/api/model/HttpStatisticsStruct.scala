package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class HttpStatisticsStruct(requestsAsync: Option[Int] = None,
                                requestsDelete: Option[Int] = None,
                                requestsGet: Option[Int] = None,
                                requestsHead: Option[Int] = None,
                                requestsOptions: Option[Int] = None,
                                requestsOther: Option[Int] = None,
                                requestsPatch: Option[Int] = None,
                                requestsPost: Option[Int] = None,
                                requestsPut: Option[Int] = None,
                                requestsTotal: Option[Int] = None)

object HttpStatisticsStruct {
  implicit val rw: ReaderWriter[HttpStatisticsStruct] = ccRW
}