package com.outr.arango.api.model

import com.outr.arango.Analyzer
import fabric.rw.{ReaderWriter, ccRW}

case class PostAPIViewLinkProps(analyzers: Option[List[Analyzer]] = None,
                                fields: Option[List[PostAPIViewFields]] = None,
                                includeAllFields: Option[Boolean] = None,
                                storeValues: Option[String] = None,
                                trackListPositions: Option[Boolean] = None)

object PostAPIViewLinkProps {
  implicit val rw: ReaderWriter[PostAPIViewLinkProps] = ccRW
}