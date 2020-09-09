package com.outr.arango

import com.outr.arango.api.OperationType
import com.outr.arango.query._

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._

trait MonitoredSupport {
  this: Graph =>

  protected def monitorFrequency: FiniteDuration = 250.millis
  protected def monitorExecutionContext: ExecutionContext = scribe.Execution.global

  object monitor extends WriteAheadLogMonitor(monitorFrequency) {
    def frequency: FiniteDuration = monitorFrequency
    def ec: ExecutionContext = monitorExecutionContext

    private var map = Map.empty[String, CollectionMonitor[_]]

    run(wal.tail()(monitorExecutionContext))(monitorExecutionContext)

    def apply[D <: Document[D]](collection: Collection[D]): CollectionMonitor[D] = synchronized {
      map.get(collection.name) match {
        case Some(cm) => cm.asInstanceOf[CollectionMonitor[D]]
        case None => {
          val cm = collection.monitor(monitor)
          map += collection.name -> cm
          cm
        }
      }
    }
  }

  implicit class CollectionMaterialized[D <: Document[D]](collection: Collection[D]) {
    def materialized(updateQuery: UpdateReferences => Query): MaterializedBuilderPart[D] = {
      MaterializedBuilderPart(collection, updateQuery)
    }
  }

  case class MaterializedBuilderPart[Base <: Document[Base]](baseCollection: Collection[Base], updateQuery: UpdateReferences => Query) {
    def into[Into <: Document[Into]](collection: WritableCollection[Into]): MaterializedBuilder[Base, Into] = {
      MaterializedBuilder(MonitoredSupport.this, baseCollection, updateQuery, collection)
    }
  }
}

case class UpdateReferences(ids: NamedRef, updatedRef: NamedRef)

case class GetReferences[D <: Document[D]](ids: NamedRef, dependencyId: Id[D])

case class Reference[D <: Document[D]](collection: Collection[D],
                                       addedQuery: GetReferences[D] => Query,
                                       removedQuery: GetReferences[D] => Query) {
  def connect[Base <: Document[Base], Into <: Document[Into]](builder: MaterializedBuilder[Base, Into]): Unit = {
    val monitor = builder.graph.monitor(collection)
    monitor.attach { op =>
      op._id.foreach { id =>
        if (op.`type` == OperationType.InsertReplaceDocument) {
          val ids = NamedRef("$ids")
          val refQuery = addedQuery(GetReferences(ids, id))
          builder.update(ids, refQuery)
        } else if (op.`type` == OperationType.RemoveDocument) {
          val ids = NamedRef("$ids")
          val refQuery = removedQuery(GetReferences(ids, id))
          builder.update(ids, refQuery)
        }
      }
    }
  }
}

case class MaterializedBuilder[Base <: Document[Base], Into <: Document[Into]](graph: Graph with MonitoredSupport,
                                                                               baseCollection: Collection[Base],
                                                                               updateQuery: UpdateReferences => Query,
                                                                               into: WritableCollection[Into],
                                                                               references: List[Reference[_ <: Document[_]]] = Nil) {
  def and[D <: Document[D]](collection: Collection[D])
                           (addedQuery: GetReferences[D] => Query)
                           (removedQuery: GetReferences[D] => Query): MaterializedBuilder[Base, Into] = {
    copy(references = references ::: List(Reference(collection, addedQuery, removedQuery)))
  }

  def build(): MaterializedBuilder[Base, Into] = {
    graph.add(init _)
    this
  }

  def init(): Future[Unit] = {
    val baseMonitor = graph.monitor(baseCollection)
    baseMonitor.attach { op =>
      op._id.foreach { id =>
        if (op.`type` == OperationType.InsertReplaceDocument) {
          // Update directly modified base value
          val ids = NamedRef("$ids")
          update(ids, aqlu"LET $ids = [$id]")
        } else if (op.`type` == OperationType.RemoveDocument) {
          val intoId = Id[Into](id.value, into.name)
          into.deleteOne(intoId)(graph.monitor.ec)
        }
      }
    }
    references.foreach(_.connect(this))
    graph.monitor.nextTick.map(_ => ())(graph.monitor.ec)
  }

  def update(ids: NamedRef, refQuery: Query): Future[Unit] = {
    val updatedRef = NamedRef("$updatedRef")
    val q = refQuery + updateQuery(UpdateReferences(ids, updatedRef)) +
      aqlu"""
            INSERT $updatedRef INTO $into OPTIONS { overwrite: true }
            RETURN u._id
          """
    graph.query(q).as[Id[Base]].results(graph.monitor.ec).map { ids =>
      scribe.debug(s"Updated: $ids")
    }(graph.monitor.ec)
  }
}