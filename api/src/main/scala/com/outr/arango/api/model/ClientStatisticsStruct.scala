package com.outr.arango.api.model

import io.circe.Json


case class ClientStatisticsStruct(bytesReceived: Option[SetofStatisticsStruct] = None,
                                  bytesSent: Option[SetofStatisticsStruct] = None,
                                  connectionTime: Option[SetofStatisticsStruct] = None,
                                  httpConnections: Option[Int] = None,
                                  ioTime: Option[SetofStatisticsStruct] = None,
                                  queueTime: Option[SetofStatisticsStruct] = None,
                                  requestTime: Option[SetofStatisticsStruct] = None,
                                  totalTime: Option[SetofStatisticsStruct] = None)