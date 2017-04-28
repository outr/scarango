package com.outr.arango.rest

case class QueryResponseStats(writesExecuted: Int,
                              writesIgnored: Int,
                              scannedFull: Int,
                              scannedIndex: Int,
                              filtered: Int,
                              executionTime: Double)
