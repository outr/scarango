package com.outr.arango

import com.outr.arango.mutation.{DataMutation, ModifyFieldValue}
import com.outr.arango.query.QueryPart
import fabric.rw._

import scala.concurrent.duration.FiniteDuration

class Field[F: ReaderWriter](val fieldName: String,
                             val mutation: Option[DataMutation] = None)
                            (implicit model: Option[DocumentModel[_]]) extends QueryPart.Support {
  model.foreach(_.defineField(this))

  object index {
    def persistent(sparse: Boolean = false,
                  unique: Boolean = false): Index = {
      Index(IndexType.Persistent, List(fieldName), sparse, unique)
    }
    def geo(geoJson: Boolean = true): Index = {
      Index(IndexType.Geo, List(fieldName), geoJson = geoJson)
    }
    def fullText(minLength: Long = 3L): Index = {
      Index(IndexType.FullText, List(fieldName), minLength = minLength)
    }
    def ttl(expireAfter: FiniteDuration): Index = {
      val seconds = expireAfter.toSeconds.toInt
      Index(IndexType.TTL, List(fieldName), expireAfterSeconds = seconds)
    }
  }

  def field[T: ReaderWriter](name: String): Field[T] = model match {
    case Some(m) => m.field[T](s"$fieldName.$name")
    case None => throw new RuntimeException("No model defined!")
  }

  def withMutation(mutation: DataMutation): Field[F] = {
    this.mutation.foreach(m => throw new RuntimeException(s"Field $fieldName already has a mutation set: $m"))
    new Field[F](fieldName, Some(mutation))
  }

  def modify(storage: F => F, retrieval: F => F): Field[F] = withMutation(ModifyFieldValue(this, storage, retrieval))

  def apply(value: F): FieldAndValue[F] = FieldAndValue(this, value.json)

  lazy val opt: Field[Option[F]] = new Field[Option[F]](fieldName, mutation)

  override def toQueryPart: QueryPart = QueryPart.Static(fieldName)
}