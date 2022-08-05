package com.outr.arango

import cats.effect.IO
import com.outr.arango.core.{ArangoDBCollection, CreateResult, DeleteResult, NotFoundException}
import fabric.Json
import fabric.rw._

case class DatabaseStore(collection: ArangoDBCollection) {
  def get[T: ReaderWriter](key: String): IO[Option[T]] = collection
    .get(id(key))
    .map(_.map(_.as[StoreValue].value.as[T]))

  def apply[T: ReaderWriter](key: String,
                             default: String => T = (key: String) => throw NotFoundException(key)): IO[T] = get[T](key)
    .map(_.getOrElse(default(key)))

  def update[T: ReaderWriter](key: String, value: T): IO[CreateResult[T]] = collection
    .upsert(StoreValue(key, value.json).json)
    .map(_.convert(_.as[T]))

  def delete(key: String): IO[DeleteResult[Json]] = collection.delete(id(key))

  def id[T](key: String): Id[T] = Id[T](key, collection.name)

  case class StoreValue(_key: String, value: Json)

  object StoreValue {
    implicit val rw: ReaderWriter[StoreValue] = ccRW
  }
}