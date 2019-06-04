package com.outr.arango.api.model

import io.circe.Json

/**
  * ClientStatisticsStruct
  *
  * @param bytesReceived *** No description ***
  * @param bytesSent *** No description ***
  * @param connectionTime *** No description ***
  * @param httpConnections the number of open http connections
  * @param ioTime *** No description ***
  * @param queueTime *** No description ***
  * @param requestTime *** No description ***
  * @param totalTime *** No description ***
  *
  * WARNING: This code is generated by youi-plugin's generateHttpClient. Do not modify directly.
  */
case class ClientStatisticsStruct(bytesReceived: Option[SetofStatisticsStruct] = None,
                                  bytesSent: Option[SetofStatisticsStruct] = None,
                                  connectionTime: Option[SetofStatisticsStruct] = None,
                                  httpConnections: Option[Int] = None,
                                  ioTime: Option[SetofStatisticsStruct] = None,
                                  queueTime: Option[SetofStatisticsStruct] = None,
                                  requestTime: Option[SetofStatisticsStruct] = None,
                                  totalTime: Option[SetofStatisticsStruct] = None)