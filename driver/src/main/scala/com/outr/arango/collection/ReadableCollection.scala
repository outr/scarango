package com.outr.arango.collection

import com.outr.arango.{Document, DocumentModel}

trait ReadableCollection[D <: Document[D], M <: DocumentModel[D]] extends Collection {
  def model: M

  def query: DocumentQuery[D, M]
}