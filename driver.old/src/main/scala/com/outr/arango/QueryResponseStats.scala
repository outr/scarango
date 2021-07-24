package com.outr.arango

import fabric.rw.{ReaderWriter, ccRW}

case class QueryResponseStats(writesExecuted: Int,
                              writesIgnored: Int,
                              scannedFull: Int,
                              scannedIndex: Int,
                              filtered: Int,
                              httpRequests: Int = -1,
                              fullCount: Int = -1,
                              executionTime: Double,
                              peakMemoryUsage: Long = -1L)

object QueryResponseStats {
  implicit val rw: ReaderWriter[QueryResponseStats] = ccRW
}