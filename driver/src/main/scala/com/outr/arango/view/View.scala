package com.outr.arango.view

import cats.effect.IO
import com.arangodb.model.arangosearch.ArangoSearchCreateOptions
import com.outr.arango.core.ArangoDB
import com.outr.arango.query.QueryPart
import com.outr.arango.util.Helpers._

class View(db: ArangoDB, val name: String, options: ArangoSearchCreateOptions) extends QueryPart.Support {
  def dbName: String = db.name

  private val view = db.db.view(name)

  def create(): IO[ViewInfo] = db.db.createArangoSearch(name, options)
    .toIO
    .map { entity =>
      ViewInfo(entity.getId, entity.getName)
    }

  def exists(): IO[Boolean] = view.exists().toIO.map(_.booleanValue())

  def drop(): IO[Unit] = view.drop().toIO.map(_ => ())

  override def toQueryPart: QueryPart = QueryPart.Static(name)
}