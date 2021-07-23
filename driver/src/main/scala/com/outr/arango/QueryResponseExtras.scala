package com.outr.arango

import fabric.rw.{ReaderWriter, ccRW}

case class QueryResponseExtras(stats: QueryResponseStats, warnings: List[Warning])

object QueryResponseExtras {
  implicit val rw: ReaderWriter[QueryResponseExtras] = ccRW
}