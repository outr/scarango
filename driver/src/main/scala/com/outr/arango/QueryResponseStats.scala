package com.outr.arango

case class QueryResponseStats(writesExecuted: Int,
                              writesIgnored: Int,
                              scannedFull: Int,
                              scannedIndex: Int,
                              filtered: Int,
                              httpRequests: Int = -1,
                              fullCount: Int = -1,
                              executionTime: Double,
                              peakMemoryUsage: Long = -1L)
