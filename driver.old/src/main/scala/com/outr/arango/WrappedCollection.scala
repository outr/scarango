package com.outr.arango

import com.outr.arango.transaction.Transaction

import scala.concurrent.{ExecutionContext, Future}

trait WrappedCollection[D <: Document[D]] extends Collection[D] {
  def collection: Collection[D]

  override def graph: Graph = collection.graph
  override def model: DocumentModel[D] = collection.model
  override def `type`: CollectionType = collection.`type`
  override def indexes: List[Index] = collection.indexes
  override def transaction: Option[Transaction] = collection.transaction
  override def get(id: Id[D])(implicit ec: ExecutionContext): Future[Option[D]] = collection.get(id)
}