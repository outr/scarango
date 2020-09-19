package com.outr.arango.monitored

import com.outr.arango.api.OperationType
import com.outr.arango.query._
import com.outr.arango.{BackingStore, Collection, Document, Graph, Id, NamedRef, Query, WritableCollection}
import reactify.Channel

import scala.concurrent.{ExecutionContext, Future}

case class Materialized[Base <: Document[Base], Into <: Document[Into]](graph: Graph with MonitoredSupport,
                                                                        base: Collection[Base],
                                                                        into: WritableCollection[Into],
                                                                        parts: List[MaterializedPart[Base, Into]]) {
  private implicit lazy val ec: ExecutionContext = graph.monitor.ec

  val updated: Channel[Id[Into]] = Channel[Id[Into]]
  val deleted: Channel[Id[Into]] = Channel[Id[Into]]

  lazy val baseRef: NamedRef = NamedRef("$base")
  lazy val ids: NamedRef = NamedRef("$ids")
  lazy val updatedRef: NamedRef = NamedRef("$updatedRef")

  private lazy val references: List[Reference[_ <: Document[_]]] = parts.flatMap(_.references(base, into))

  private lazy val updateQuery: Query = {
    val pre =
      aqlu"""
           FOR $baseRef IN $base
           FILTER $baseRef._id IN $ids
           LET $updatedRef = {
        """
    val post =
      aqlu"""
               _key: $baseRef._key
             }
          """
    val info = QueryInfo(baseRef, ids, updatedRef)
    val queries = parts
      .flatMap(_.updateQueryPart(info))
      .map { query =>
        val trimmed = query.value.trim
        if (trimmed.endsWith(",")) {
          query
        } else {
          query.copy(value = s"$trimmed,\n")
        }
      }
    Query.merge(List(pre) ::: queries ::: List(post))
  }

  graph.add(init _)

  private def init(): Future[Unit] = {
    val baseMonitor = graph.monitor(base)
    baseMonitor.attach { op =>
      op._id.foreach { id =>
        if (op.`type` == OperationType.InsertReplaceDocument) {
          // Update directly modified base value
          update(aqlu"LET $ids = [$id]")
        } else if (op.`type` == OperationType.RemoveDocument) {
          val intoId = Id[Into](id.value, into.name)
          val future = into.deleteOne(intoId).map { id =>
            deleted @= Id[Into](id.value, into.name)
          }
          future.failed.foreach(t => scribe.error(t))
        }
      }
    }
    references.groupBy(_.collection).values.foreach { references =>   // Group and merge to avoid multiple query invocations
      val reference = Reference.merge[BackingStore](references.asInstanceOf[List[Reference[BackingStore]]])
      reference.connect(this)
    }
    graph.monitor.nextTick.map(_ => ())
  }

  def update(refQuery: Query): Future[Unit] = {
    val q = refQuery + updateQuery +
      aqlu"""
            INSERT $updatedRef INTO $into OPTIONS { overwrite: true }
            RETURN $$base._id
          """

    val future = graph.query(q).as[Id[Base]].results.map { updated =>
      updated.foreach(id => this.updated @= Id[Into](id.value, into.name))
    }
    future.failed.foreach(t => scribe.error(t))
    future
  }
}