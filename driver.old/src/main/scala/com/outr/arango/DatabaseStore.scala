package com.outr.arango

import fabric.rw._
import reactify.Var

import scala.concurrent.{ExecutionContext, Future}

case class DatabaseStore[T: ReaderWriter](key: String, graph: Graph) {
  def get(implicit ec: ExecutionContext): Future[Option[T]] = {
    graph.backingStore.get(BackingStore.id(key)).map(_.map(_.data.as[T]))
  }
  def apply(default: => T)(implicit ec: ExecutionContext): Future[T] = {
    get(ec).map(_.getOrElse(default))
  }
  def set(value: T)(implicit ec: ExecutionContext): Future[Unit] = {
    val json = value.toValue
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