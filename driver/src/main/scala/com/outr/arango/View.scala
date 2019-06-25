package com.outr.arango

import scala.concurrent.{ExecutionContext, Future}

class View[D <: Document[D]](val name: String,
                             val fields: List[Field[_]],
                             val collection: Collection[D]) {
  lazy val arangoView: ArangoView = collection.graph.arangoDatabase.searchView(name)

  collection.graph.add(this)

  protected[arango] def create(createView: Boolean)(implicit ec: ExecutionContext): Future[Unit] = for {
    _ <- if (createView) {
      arangoView.create()           // TODO: support configuration
    } else {
      Future.successful(())
    }
    _ <- arangoView.update(links = Some(List(
      ViewLink(
        collectionName = collection.name,
        fields = fields.map(_.name)
      )
    )))
  } yield {
    ()
  }
}
