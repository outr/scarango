package com.outr.arango.collection

import com.outr.arango.{Document, DocumentModel}

trait ReadableCollection[D <: Document[D]] extends Collection {
  def model: DocumentModel[D]

  def query: DocumentCollectionQuery[D]
}