package com.outr.arango.api.model

import io.circe.Json


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