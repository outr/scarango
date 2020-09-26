package com.outr.arango.monitored

import com.outr.arango.{Collection, Document, Field, Graph, Id, NamedRef, Query, WritableCollection}
import com.outr.arango.query._
import io.youi.Unique

case class MaterializedBuilder[Base <: Document[Base], Into <: Document[Into]](graph: Graph with MonitoredSupport,
                                                                               base: Collection[Base],
                                                                               into: WritableCollection[Into],
                                                                               parts: List[MaterializedPart[Base, Into]]) {

  def withPart(part: MaterializedPart[Base, Into]): MaterializedBuilder[Base, Into] = {
    copy(parts = parts ::: List(part))
  }

  def withField[T](field: Field[T])(f: QueryInfo => Query): MaterializedBuilder[Base, Into] = withPart(new MaterializedPart[Base, Into] {
    override def updateQueryPart(info: QueryInfo): Option[Query] = {
      val query = f(info)
      Some(aqlu"$field: " + query)
    }

    override def references(base: Collection[Base], into: WritableCollection[Into]): List[Reference[_ <: Document[_]]] = Nil
  })

  def map[T](mapping: (Field[T], Field[T])): MaterializedBuilder[Base, Into] = withPart(new MaterializedPart[Base, Into] {
    override def updateQueryPart(info: QueryInfo): Option[Query] = {
      val (f1, f2) = mapping
      Some(aqlu"$f2: ${info.baseRef}.$f1,")
    }

    override def references(base: Collection[Base], into: WritableCollection[Into]): List[Reference[_ <: Document[_]]] = Nil
  })

  def one2Many[D <: Document[D]](collection: Collection[D],
                                    baseIdRef: Field[Id[Base]],
                                    field: Field[List[D]]): MaterializedBuilder[Base, Into] = withPart(new MaterializedPart[Base, Into] {
    override def updateQueryPart(info: QueryInfo): Option[Query] = {
      val collectionRef = NamedRef()
      Some(
        aqlu"""
             $field: (
                FOR $collectionRef IN $collection
                FILTER $collectionRef.$baseIdRef == ${info.baseRef}._id
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