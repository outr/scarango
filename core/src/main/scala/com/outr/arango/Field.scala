package com.outr.arango

import com.outr.arango.mutation.{DataMutation, ModifyFieldValue}
import com.outr.arango.query.QueryPart
import fabric.rw._

import scala.concurrent.duration.FiniteDuration

class Field[F](val fieldName: String,
               val mutation: Option[DataMutation])
              (implicit rw: RW[F], model: Option[DocumentModel[_]]) extends QueryPart.Support {
  def this(fieldName: String, isArray: Boolean)(implicit rw: RW[F], model: Option[DocumentModel[_]]) = {
    this(if (isArray) s"$fieldName[*]" else fieldName, None)(rw, model)
  }

  def this(fieldName: String)(implicit rw: RW[F], model: Option[DocumentModel[_]]) = {
    this(fieldName, isArray = false)(rw, model)
  }

  model.foreach(_.defineField(this))

  object index {
    def persistent(sparse: Boolean = false,
                   unique: Boolean = false): Index = {
      Index(IndexType.Persistent, List(fieldName), sparse, unique)
    }

    def geo(geoJson: Boolean = true): Index = {
      Index(IndexType.Geo, List(fieldName), geoJson = geoJson)
    }

    def ttl(expireAfter: FiniteDuration): Index = {
      val seconds = expireAfter.toSeconds.toInt
      Index(IndexType.TTL, List(fieldName), expireAfterSeconds = seconds)
    }
  }

  def field[T: RW](name: String): Field[T] = model match {
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