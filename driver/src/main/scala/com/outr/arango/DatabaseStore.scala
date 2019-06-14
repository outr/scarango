package com.outr.arango

import reactify.Var

import scala.concurrent.{ExecutionContext, Future}

case class DatabaseStore[T](key: String, graph: Graph, serialization: Serialization[T]) {
  def get(implicit ec: ExecutionContext): Future[Option[T]] = {
    graph.backingStore.get(BackingStore.id(key)).map(_.map(bs => serialization.fromJson(bs.data)))
  }
  def apply(default: => T)(implicit ec: ExecutionContext): Future[T] = {
    get(ec).map(_.getOrElse(default))
  }
  def set(value: T)(implicit ec: ExecutionContext): Future[Unit] = {
    val json = serialization.toJson(value)
    graph.backingStore.upsertOne(BackingStore(json, BackingStore.id(key))).map(_ => ())
  }

  def prop(default: => T)
          (implicit ec: ExecutionContext): Future[Var[T]] = get.map { o =>
    val initial = o.getOrElse(default)
    val v = Var[T](initial)
    v.attach(set)
    v
  }
}