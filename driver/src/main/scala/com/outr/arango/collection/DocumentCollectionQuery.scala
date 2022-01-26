package com.outr.arango.collection

import cats.effect.IO
import com.outr.arango.{Document, DocumentModel, DocumentRef, Field, FieldAndValue}
import com.outr.arango.query.{Query, SortDirection}
import com.outr.arango.query.dsl._
import fabric.rw.ReaderWriter

class DocumentCollectionQuery[D <: Document[D]](collection: DocumentCollection[D]) extends DocumentQuery[D] {
  override def apply(query: Query): fs2.Stream[IO, D] = collection.graph.queryAs[D](query)(collection.model.rw)

  override def as[T](query: Query)(implicit rw: ReaderWriter[T]): fs2.Stream[IO, T] = collection.graph.queryAs[T](query)(rw)

  override def all(query: Query): IO[List[D]] = apply(query).compile.toList

  override def one(query: Query): IO[D] = all(query).map {
    case Nil => throw new RuntimeException("No results")
    case d :: Nil => d
    case list => throw new RuntimeException(s"More than one result returned: $list")
  }

  override def first(query: Query): IO[Option[D]] = apply(query).take(1).compile.last

  override def last(query: Query): IO[Option[D]] = apply(query).compile.last

  def byFilter(filter: Filter,
               sort: (Field[_], SortDirection) = collection.model._id.desc): fs2.Stream[IO, D] = {
    val d: DocumentRef[D, DocumentModel[D]] = DocumentRef(collection.model, Some("d"))
    apply(aql {
      FOR (d) IN collection
      FILTER(filter)
      SORT (sort.asInstanceOf[(Field[Any], SortDirection)])
      RETURN (d)
    })
  }
}