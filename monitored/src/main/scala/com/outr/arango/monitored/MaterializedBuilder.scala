package com.outr.arango.monitored

import com.outr.arango.api.OperationType
import com.outr.arango.{Collection, Document, Field, Graph, Id, NamedRef, Query, WritableCollection}
import com.outr.arango.query._
import io.youi.Unique

import scala.concurrent.{ExecutionContext, Future}

case class MaterializedBuilder[Base <: Document[Base], Into <: Document[Into]](graph: Graph with MonitoredSupport,
                                                                               base: Collection[Base],
                                                                               into: WritableCollection[Into],
                                                                               parts: List[MaterializedPart[Base, Into]]) {

  def withPart(part: MaterializedPart[Base, Into]): MaterializedBuilder[Base, Into] = {
    copy(parts = parts ::: List(part))
  }

  def map[T](mapping: (Field[T], Field[T])): MaterializedBuilder[Base, Into] = withPart(new MaterializedPart[Base, Into] {
    override def updateQueryPart(baseRef: NamedRef, ids: NamedRef, updatedRef: NamedRef): Option[Query] = {
      val (f1, f2) = mapping
      Some(aqlu"$f2: $baseRef.$f1,")
    }

    override def references(base: Collection[Base], into: WritableCollection[Into]): List[Reference[_ <: Document[_]]] = Nil
  })

  def one2Many[D <: Document[D], B](collection: Collection[D],
                                    baseIdRef: Field[Id[B]],
                                    field: Field[List[D]]): MaterializedBuilder[Base, Into] = withPart(new MaterializedPart[Base, Into] {
    override def updateQueryPart(baseRef: NamedRef, ids: NamedRef, updatedRef: NamedRef): Option[Query] = {
      val collectionRef = NamedRef(s"$$coll_${field.fieldName}")
      Some(
        aqlu"""
             $field: (
                FOR $collectionRef IN $collection
                FILTER $collectionRef.$baseIdRef IN $ids
                RETURN $collectionRef
             ),
          """)
    }

    override def references(base: Collection[Base], into: WritableCollection[Into]): List[Reference[_ <: Document[_]]] = {
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
  })

  def build(): Materialized[Base, Into] = Materialized(graph, base, into, parts)
}