package com.outr.arango

import com.outr.arango.api.{OperationType, WALOperation}
import profig.JsonUtil

case class Operation[D <: Document[D]](underlying: WALOperation, graph: Graph) {
  private lazy val data = JsonUtil.fromJson[Data](underlying.data)

  def db: String = underlying.db

  def `type`: OperationType = underlying.`type`
  lazy val _id: Option[Id[D]] = data._id.orElse(_key.flatMap { key =>
    collectionName.map(cn => Id[D](key, cn))
  })
  lazy val _key: Option[String] = data._key
  lazy val collection: Option[Collection[D]] = underlying.collectionId.flatMap { cid =>
    graph.collections.find(_.id == cid).asInstanceOf[Option[Collection[D]]]
  }
  lazy val collectionName: Option[String] = collection.map(_.name)

  case class Data(_id: Option[Id[D]], _key: Option[String], _rev: Option[String])
}