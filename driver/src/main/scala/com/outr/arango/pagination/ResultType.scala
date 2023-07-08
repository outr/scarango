package com.outr.arango.pagination

import fabric.rw.RW

sealed trait ResultType

object ResultType {
  implicit lazy val rw: RW[ResultType] = RW.enumeration(List(Reference, Cached, CachedUpdated))

  /**
    * Maintains a reference to the document only. This leads to the latest document information being accessible while
    * paging through results. However, it can lead to document loss if deletions occur.
    */
  case object Reference extends ResultType

  /**
    * Maintains a cached copy of the document in the pagination results that leads to guaranteed consistent results as
    * the pages are consumed. However, updates to documents will not be reflected in the results.
    */
  case object Cached extends ResultType

  /**
    * Maintains a cached copy of the document, but during fetch it will attempt to update the document with the latest
    * information if available. This leads to a hybrid of Reference and Cached where deleted data will not lead to loss,
    * and the page will always have the latest information.
    */
  case object CachedUpdated extends ResultType
}