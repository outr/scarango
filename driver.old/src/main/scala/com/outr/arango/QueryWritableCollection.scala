package com.outr.arango

import com.outr.arango.query._
import com.outr.arango.transaction.Transaction
import fabric.rw.ReaderWriter

import scala.concurrent.{ExecutionContext, Future}

trait QueryWritableCollection[D <: Document[D]] extends WritableCollection[D] {
  private implicit def rw: ReaderWriter[D] = model.rw

  lazy val all: QueryBuilder[D] = graph.query(Query(s"FOR c IN $name RETURN c", Map.empty), transaction).as[D]

  override def withTransaction(transaction: Transaction): QueryWritableCollection[D] = new TransactionCollection[D](this, transaction) with QueryWritableCollection[D]

  def query(query: Query): QueryBuilder[D] = graph.query(query, transaction).as[D]

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
    this.query(query).as[Long]((json: fabric.Value) => json.getLong.getOrElse(0L)).one
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
    this.query(query).as[Long]((json: fabric.Value) => json.getLong.getOrElse(0L)).one
  }
}
