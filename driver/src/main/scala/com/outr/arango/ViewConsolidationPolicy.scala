package com.outr.arango

import fabric.rw.{ReaderWriter, ccRW}

case class ViewConsolidationPolicy(`type`: String, threshold: Option[BigDecimal])

object ViewConsolidationPolicy {
  implicit val rw: ReaderWriter[ViewConsolidationPolicy] = ccRW
}