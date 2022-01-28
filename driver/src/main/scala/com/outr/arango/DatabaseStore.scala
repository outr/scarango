package com.outr.arango

import cats.effect.IO
import com.outr.arango.core.{ArangoDBCollection, CreateResult, DeleteResult, NotFoundException}
import fabric.Value
import fabric.rw._

case class DatabaseStore(collection: ArangoDBCollection) {
  def get[T: ReaderWriter](key: String): IO[Option[T]] = collection
    .get(key)
    .map(_.map(_.as[StoreValue].value.as[T]))

  def apply[T: ReaderWriter](key: String,
                             default: String => T = (key: String) => throw NotFoundException(key)): IO[T] = get[T](key)
    .map(_.getOrElse(default(key)))

  def update[T: ReaderWriter](key: String, value: T): IO[CreateResult[T]] = collection
    .upsert(StoreValue(key, value.toValue).toValue)
    .map(_.convert(_.as[T]))

  def delete(key: String): IO[DeleteResult[Value]] = collection.delete(key)

  case class StoreValue(_key: String, value: Value)

  object StoreValue {
    implicit val rw: ReaderWriter[StoreValue] = ccRW
  }
}