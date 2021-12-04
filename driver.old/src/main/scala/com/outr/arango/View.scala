package com.outr.arango

import com.outr.arango.api.model.ArangoLinkFieldProperties

import scala.concurrent.{ExecutionContext, Future}

class View[D <: Document[D]](val name: String,
                             val includeAllFields: Boolean,
                             val fields: Map[Field[_], ArangoLinkFieldProperties],
                             val collection: Collection[D],
                             val analyzers: List[Analyzer]) {
  lazy val arangoView: ArangoView = collection.graph.arangoDatabase.searchView(name)

  collection.graph.add(this)

  protected[arango] def create(createView: Boolean)(implicit ec: ExecutionContext): Future[Unit] = for {
    _ <- if (createView) {
      arangoView.create()
    } else {
      Future.successful(())
    }
    _ <- arangoView.update(
      includeAllFields = includeAllFields,
      links = Some(List(
        ViewLink(
          collectionName = collection.name,
          fields = fields.map {
            case (f, p) => f.fieldName -> p
          },
          analyzers = analyzers
        )
      )
    ))
  } yield {
    ()
  }
}