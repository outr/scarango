package com.outr.arango

import fabric.rw.{ReaderWriter, ccRW}

case class IndexInfo(`type`: String,
                     fields: Option[List[String]] = None,
                     unique: Option[Boolean] = None,
                     sparse: Option[Boolean] = None,
                     id: Id[Index],
                     isNewlyCreated: Option[Boolean] = None,
                     selectivityEstimate: Option[Double] = None,
                     error: Boolean = false,
                     code: Int = 0)

object IndexInfo {
  implicit val rw: ReaderWriter[IndexInfo] = ccRW
}