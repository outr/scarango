package com.outr.arango

import cats.effect.IO
import com.outr.arango.query.Query

trait ReadableCollection[D <: Document[D]] extends Collection {
  def model: DocumentModel[D]

  def query(query: Query): fs2.Stream[IO, D]
}