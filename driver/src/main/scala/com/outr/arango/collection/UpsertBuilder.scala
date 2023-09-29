package com.outr.arango.collection

import cats.effect.IO
import com.outr.arango.query._
import com.outr.arango.query.dsl._
import com.outr.arango.{Document, DocumentModel, DocumentRef, FieldAndValue}
import fabric.{Json, Str}
import fabric.io.JsonFormatter
import fabric.rw._

case class UpsertBuilder[D <: Document[D], M <: DocumentModel[D]](collection: DocumentCollection[D, M],
                                                                  search: List[(String, QueryPart)] = Nil,
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

  def withSearch(f: FieldAndValue[_]): UpsertBuilder[D, M] =
    withSearch(f.field.fieldName, QueryPart.Variable(f.value))
  def withSearch(entry: (String, QueryPart)): UpsertBuilder[D, M] = copy(
    search = entry :: search
  )
  def withListSearch[T <: Document[T], TM <: DocumentModel[T]](list: List[T])
                                                              (f: DocumentRef[T, TM] => List[SearchEntry]): UpsertBuilder[D, M] = {
    ???
  }
  def withInsert(doc: D): UpsertBuilder[D, M] = withInsert(doc.json(collection.model.rw))
  def withInsert(json: Json): UpsertBuilder[D, M] = withInsert(JsonFormatter.Compact(json))
  def withInsert(insert: String): UpsertBuilder[D, M] = copy(insert = Some(insert))

  def withUpdate(doc: D): UpsertBuilder[D, M] = withUpdate(doc.json(collection.model.rw))
  def withUpdate(json: Json): UpsertBuilder[D, M] = withUpdate(JsonFormatter.Compact(json))
  def withUpdate(update: String): UpsertBuilder[D, M] = copy(upsert = Some(Upsert.Update(update)))

  def withReplace(doc: D): UpsertBuilder[D, M] = copy(upsert = Some(Upsert.Replace(doc)))

  def withOptions(ignoreErrors: Boolean = false,
                  keepNull: Boolean = true,
                  mergeObjects: Boolean = true,
                  waitForSync: Boolean = false,
                  ignoreRevs: Boolean = true,
                  exclusive: Boolean = false,
                  indexHint: Option[String] = None,
                  forceIndexHint: Boolean = false): UpsertBuilder[D, M] = copy(
    ignoreErrors = ignoreErrors,
    keepNull = keepNull,
    mergeObjects = mergeObjects,
    waitForSync = waitForSync,
    ignoreRevs = ignoreRevs,
    exclusive = exclusive,
    indexHint = indexHint,
    forceIndexHint = forceIndexHint
  )

  def toQuery(includeReturn: Boolean): Query = {
    assert(search.nonEmpty, "At least one search criteria must be defined")
    assert(insert.nonEmpty, "Insert must be defined")
    assert(upsert.nonEmpty, "Update or Replace must be defined")

    val commaPart = QueryPart.Static(", ")
    val searchQuery = Query(search.flatMap {
      case (key, value) => List(
        commaPart,
        QueryPart.Static(s"$key: "),
        value
      )
    }.tail)

    val upsertQuery = aql"""UPSERT { $searchQuery }"""
    val insertQuery = aql"""INSERT ${insert.get}"""
    val updateReplaceQuery = upsert.get match {
      case Upsert.Update(value) => aql"""UPDATE ${QueryPart.Static(value)} IN $collection"""
      case Upsert.Replace(replacement) => aql"""REPLACE $replacement IN $collection"""
    }
    var queries = List(upsertQuery, insertQuery, updateReplaceQuery)
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
}

sealed trait Upsert[D <: Document[D]]

object Upsert {
  case class Update[D <: Document[D]](value: String) extends Upsert[D]
  case class Replace[D <: Document[D]](replacement: D) extends Upsert[D]
}

case class UpsertResult[D](original: Option[D], newValue: D)

object UpsertResult {
  implicit def rw[D: RW]: RW[UpsertResult[D]] = RW.gen
}

sealed trait SearchEntry