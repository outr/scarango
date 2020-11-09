package com.outr.arango

import com.outr.arango.query._
import com.outr.arango.transaction.Transaction
import io.circe.Json

import scala.concurrent.{ExecutionContext, Future}

trait QueryWritableCollection[D <: Document[D]] extends WritableCollection[D] {
  lazy val all: QueryBuilder[D] = graph.query(Query(s"FOR c IN $name RETURN c", Map.empty), transaction).as[D](model.serialization)

  override def withTransaction(transaction: Transaction): QueryWritableCollection[D] = new TransactionCollection[D](this, transaction, replicationFactor) with QueryWritableCollection[D]

  def query(query: Query): QueryBuilder[D] = graph.query(query, transaction).as[D](model.serialization)

  def update(filter: => Filter, fieldAndValues: FieldAndValue[_]*)
            (implicit ec: ExecutionContext): Future[Long] = {
    val v = DocumentRef[D, DocumentModel[D]](model, None)
    val count = ref("count")

    val query = aql {
      FOR(v) IN this
      FILTER(withReference(v)(filter))
      UPDATE(v, fieldAndValues: _*)
      COLLECT WITH COUNT INTO count
      RETURN(count)
    }
    this.query(query).as[Long]((json: Json) => json.asNumber.flatMap(_.toLong).getOrElse(0L)).one
  }

  def updateAll(fieldAndValues: FieldAndValue[_]*)(implicit ec: ExecutionContext): Future[Long] = {
    val v = DocumentRef[D, DocumentModel[D]](model, None)
    val count = ref("count")
    val query = aql {
      FOR(v) IN this
      UPDATE(v, fieldAndValues: _*)
      COLLECT WITH COUNT INTO count
      RETURN(count)
    }
    this.query(query).as[Long]((json: Json) => json.asNumber.flatMap(_.toLong).getOrElse(0L)).one
  }
}
