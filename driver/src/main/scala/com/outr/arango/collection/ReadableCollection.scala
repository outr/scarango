package com.outr.arango.collection

import cats.effect.IO
import com.outr.arango.query.Query
import com.outr.arango.{Document, DocumentModel}

trait ReadableCollection[D <: Document[D]] extends Collection {
  def model: DocumentModel[D]

  def query(query: Query): fs2.Stream[IO, D]
}
