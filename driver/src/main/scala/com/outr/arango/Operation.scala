package com.outr.arango

import com.outr.arango.api.{OperationType, WALOperation}
import profig.JsonUtil

case class Operation[D <: Document[D]](underlying: WALOperation, graph: Graph) {
  private lazy val data = JsonUtil.fromJson[Data](underlying.data)

  def db: String = underlying.db

  def `type`: OperationType = underlying.`type`
  lazy val _id: Option[Id[D]] = data._id
  lazy val _key: Option[String] = _id.map(_._key)
  lazy val collectionName: Option[String] = _id.map(_.collection)
  lazy val collection: Option[Collection[D]] = collectionName.flatMap(n => graph.collections.find(_.name == n)).asInstanceOf[Option[Collection[D]]]

  case class Data(_id: Option[Id[D]], _rev: Option[String])
}