package com.outr.arango

case class QueryResponseStats(writesExecuted: Int,
                              writesIgnored: Int,
                              scannedFull: Int,
                              scannedIndex: Int,
                              filtered: Int,
                              executionTime: Double)
