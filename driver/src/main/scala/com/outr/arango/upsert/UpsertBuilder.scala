package com.outr.arango.upsert

import cats.effect.IO
import com.outr.arango.collection.DocumentCollection
import com.outr.arango.query._
import com.outr.arango.query.dsl._
import com.outr.arango.{Document, DocumentModel, DocumentRef, Field, FieldAndValue, Ref}
import fabric._
import fabric.io.JsonFormatter
import fabric.rw._

case class UpsertBuilder[D <: Document[D]](collection: DocumentCollection[D],
                                                                  list: Option[() => (Ref, List[Json], List[QueryPart])] = None,
                                                                  search: List[QueryPart] = Nil,
                                                                  insert: Option[Json] = None,
                                                                  upsert: Option[Upsert[D]] = None,
                                                                  ignoreErrors: Boolean = false,
                                                                  keepNull: Boolean = true,
                                                                  mergeObjects: Boolean = true,
                                                                  waitForSync: Boolean = false,
                                                                  ignoreRevs: Boolean = true,
                                                                  exclusive: Boolean = false,
                                                                  indexHint: Option[String] = None,
                                                                  forceIndexHint: Boolean = false) {
  private implicit def rw: RW[D] = collection.model.rw

  def withSearch(f: FieldAndValue[_]): UpsertBuilder[D] =
    withSearch(f.field.fieldName, QueryPart.Variable(f.value))
  def withSearch(entry: (String, QueryPart)): UpsertBuilder[D] = {
    val part = QueryPart.Container(List(QueryPart.Static(entry._1), QueryPart.Static(": "), entry._2))
    copy(
      search = part :: search
    )
  }
  def withListSearch[T <: Document[T]](collection: DocumentCollection[T], list: List[T])
                                                              (f: DocumentRef[T] => List[Searchable]): UpsertBuilder[D] = {
    copy(
      list = Some(() => {
        val ref = collection.ref("doc")
        val entries = f(ref).map(_.toSearch)
        (ref, list.map(_.json(collection.model.rw)), entries)
      })
    )
  }
  def withListSearch(list: List[D])(f: DocumentRef[D] => List[Searchable]): UpsertBuilder[D] =
    withListSearch[D](collection, list)(f)
  def withInsert(doc: D): UpsertBuilder[D] = withInsert(doc.json(collection.model.rw))
  def withInsert(json: Json): UpsertBuilder[D] = withInsert(JsonFormatter.Compact(json))
  def withInsert(insert: String): UpsertBuilder[D] = copy(insert = Some(insert))

  def withUpdate(doc: D): UpsertBuilder[D] = withUpdate(doc.json(collection.model.rw))
  def withUpdate(json: Json): UpsertBuilder[D] = withUpdate(JsonFormatter.Compact(json))
  def withUpdate(update: String): UpsertBuilder[D] = copy(upsert = Some(Upsert.Update(update)))
  def withNoUpdate: UpsertBuilder[D] = withUpdate(obj())

  def withReplace(doc: D): UpsertBuilder[D] = copy(upsert = Some(Upsert.Replace(doc)))

  def withOptions(ignoreErrors: Boolean = false,
                  keepNull: Boolean = true,
                  mergeObjects: Boolean = true,
                  waitForSync: Boolean = false,
                  ignoreRevs: Boolean = true,
                  exclusive: Boolean = false,
                  indexHint: Option[String] = None,
                  forceIndexHint: Boolean = false): UpsertBuilder[D] = copy(
    ignoreErrors = ignoreErrors,
    keepNull = keepNull,
    mergeObjects = mergeObjects,
    waitForSync = waitForSync,
    ignoreRevs = ignoreRevs,
    exclusive = exclusive,
    indexHint = indexHint,
    forceIndexHint = forceIndexHint
  )

  def toQuery(includeReturn: Boolean): Query = noConsumingRefs {
    assert(search.nonEmpty || list.nonEmpty, "At least one search criteria must be defined")
    assert(insert.nonEmpty || list.nonEmpty, "Insert must be defined")
    assert(upsert.nonEmpty || upsert.nonEmpty || list.nonEmpty, "Update or Replace must be defined")

    var searchEntries = search

    val listValue = list.map(f => f())
    val forQuery = listValue.map {
      case (ref, list, entries) =>
        searchEntries = searchEntries ::: entries
        aql"""FOR ${QueryPart.Ref(ref)} IN $list"""
    }

    val commaPart = QueryPart.Static(", ")
    val searchQuery = Query(searchEntries.flatMap { part =>
      List(
        commaPart,
        part
      )
    }.tail)

    val upsertQuery = aql"""UPSERT { $searchQuery }"""
    val insertQuery = insert match {
      case Some(i) => aql"""INSERT $i"""
      case None =>
        val ref = listValue.get._1
        aql"""INSERT ${QueryPart.Ref(ref)}"""
    }
    val updateReplaceQuery = upsert.map {
      case Upsert.Update(value) => aql"""UPDATE ${QueryPart.Static(value)} IN $collection"""
      case Upsert.Replace(replacement) => aql"""REPLACE $replacement IN $collection"""
      case _ => throw new RuntimeException("Should not be possible, but Scala 3 says it is...")
    }.getOrElse {
      val ref = listValue.get._1
      aql"""REPLACE ${QueryPart.Ref(ref)} IN $collection"""
    }
    var queries = List(upsertQuery, insertQuery, updateReplaceQuery)
    forQuery.foreach { q =>
      queries = q :: queries
    }
    if (includeReturn) {
      val returnQuery = aql"""RETURN { original: OLD, newValue: NEW }"""
      queries = queries ::: List(returnQuery)
    }
    Query.merge(queries)
  }

  def toStream: fs2.Stream[IO, UpsertResult[D]] = {
    val query = toQuery(includeReturn = true)
    collection.graph.query[UpsertResult[D]](query).stream()
  }

  def toList: IO[List[UpsertResult[D]]] = toStream.compile.toList

  def execute(): IO[Unit] = {
    val query = toQuery(includeReturn = false)
    collection.graph.execute(query)
  }
}