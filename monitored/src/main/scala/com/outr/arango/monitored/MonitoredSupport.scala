package com.outr.arango.monitored

import com.outr.arango._
import com.outr.arango.api.OperationType
import com.outr.arango.query._
import io.youi.Unique

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import scala.language.implicitConversions

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

//  case class MaterializedPart(updateQueryPart: (NamedRef, UpdateReferences) => Query, references: List[Reference[_ <: Document[_]]])

  trait MaterializedPart {
    def updateQueryPart(baseRef: NamedRef, updatedReferences: UpdateReferences): Query
    def references[Base <: Document[Base], Into <: Document[Into]](base: Collection[Base], into: WritableCollection[Into]): List[Reference[_ <: Document[_]]]
  }

  implicit def fieldTupleMapping[T](tuple: (Field[T], Field[T])): MaterializedPart = {
    val (f1, f2) = tuple
    new MaterializedPart {
      override def updateQueryPart(baseRef: NamedRef, updatedReferences: UpdateReferences): Query = {
        aqlu"$f2: $baseRef.$f1,"
      }

      override def references[Base <: Document[Base], Into <: Document[Into]](base: Collection[Base], into: WritableCollection[Into]): List[Reference[_ <: Document[_]]] = Nil
    }
  }

  def one2Many[D <: Document[D], B](collection: Collection[D],
                                    baseIdRef: Field[Id[B]],
                                    field: Field[List[D]]): MaterializedPart = new MaterializedPart {
    override def updateQueryPart(baseRef: NamedRef, updatedReferences: UpdateReferences): Query = {
      val collectionRef = NamedRef(s"$$coll_${field.fieldName}")
      aqlu"""
             $field: (
                FOR $collectionRef IN $collection
                FILTER $collectionRef.$baseIdRef IN ${updatedReferences.ids}
                RETURN $collectionRef
             ),
          """
    }

    override def references[Base <: Document[Base], Into <: Document[Into]](base: Collection[Base], into: WritableCollection[Into]): List[Reference[_ <: Document[_]]] = {
      val reference = Reference(
        collection = collection,
        addedQuery = (refs: GetReferences[D]) => {
          aqlu"LET ${refs.ids} = [DOCUMENT(${refs.dependencyId}).$baseIdRef]"
        },
        removedQuery = (refs: GetReferences[D]) => {
          val subQuery = NamedRef(s"$$sub${Unique(length = 8)}")
          aqlu"""
               LET ${refs.ids} = (
                 FOR $subQuery IN $into
                 FILTER ${refs.dependencyId} IN $subQuery.$field[*]._id
                 RETURN CONCAT(${base.name + "/"}, $subQuery._key)
               )
            """
        }
      )
      List(reference)
    }
  }

  def materialized[Base <: Document[Base], Into <: Document[Into]](baseInto: (Collection[Base], WritableCollection[Into]),
                                                                   parts: MaterializedPart*): Unit = {
    val (base, into) = baseInto
    val baseRef = NamedRef("$base")
    val updateQuery = (refs: UpdateReferences) => {
      val pre =
        aqlu"""
             FOR $baseRef IN $base
             FILTER $baseRef._id IN ${refs.ids}
             LET ${refs.updatedRef} = {
          """
      val post =
        aqlu"""
                 _key: $baseRef._key
               }
            """
      Query.merge(List(pre) ::: parts.toList.map(_.updateQueryPart(baseRef, refs)) ::: List(post))
    }

    MaterializedBuilder[Base, Into](
      graph = this,
      baseCollection = base,
      updateQuery = updateQuery,
      into = into,
      references = parts.flatMap(_.references(base, into)).toList
    ).build()
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
            RETURN $$base._id
          """
    graph.query(q).as[Id[Base]].results(graph.monitor.ec).map(_ => ())(graph.monitor.ec)
  }
}