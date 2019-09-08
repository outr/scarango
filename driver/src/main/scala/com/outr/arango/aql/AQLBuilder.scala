package com.outr.arango.aql

import com.outr.arango.{Collection, Document, DocumentModel, DocumentRef, Field, Query, Value}

import scala.annotation.tailrec

class AQLBuilder(val parts: List[QueryPart] = Nil) {
  private var map = Map.empty[String, Int]
  private var used = Set.empty[String]

  private[aql] def ref2Name[D <: Document[D], Model <: DocumentModel[D]](ref: DocumentRef[D, Model]): String = {
    createName(ref.collectionName, ref.id, 1)
  }

  @tailrec
  private[aql] final def createName(collectionName: String, id: String, position: Int): String = {
    map.get(id) match {
      case Some(i) => s"${collectionName.charAt(0)}$i"
      case None => {
        val name = s"${collectionName.charAt(0)}$position"
        if (!used.contains(name)) {
          used += name
          map += id -> position
          name
        } else {
          createName(collectionName, id, position + 1)
        }
      }
    }
  }

  def FOR[D <: Document[D], Model <: DocumentModel[D]](ref: DocumentRef[D, Model]): ForPartial[D, Model] = {
    ForPartial(ref, this)
  }
  def FILTER(filter: Filter): AQLBuilder = withPart(FilterPart(filter))
  def SORT[T](f: => (Field[T], SortDirection)): AQLBuilder = withPart(SortPart[T](() => f))
  def RETURN[D <: Document[D], Model <: DocumentModel[D]](ref: DocumentRef[D, Model]): AQLBuilder = {
    withPart(ReturnPart(ref))
  }

  def withPart(part: QueryPart): AQLBuilder = new AQLBuilder(parts ::: List(part))
  def toQuery: Query = {
    val queries = parts.map(_.build(this))
    Query.merge(queries)
  }
}

sealed trait SortDirection

object SortDirection {
  case object ASC extends SortDirection
  case object DESC extends SortDirection
}

trait QueryPart {
  def build(builder: AQLBuilder): Query
}

case class ForPartial[D <: Document[D], Model <: DocumentModel[D]](ref: DocumentRef[D, Model], builder: AQLBuilder) {
  def IN(collection: Collection[D]): AQLBuilder = builder.withPart(ForPart(ref, collection))
}

case class SortPart[T](f: () => (Field[T], SortDirection)) extends QueryPart {
  override def build(builder: AQLBuilder): Query = {
    val (refOption, (field, sort)) = withReference(f())
    val ref = refOption.getOrElse(throw new RuntimeException("No ref option found for SORT!"))
    val name = createName(ref.model.asInstanceOf[DocumentModel[_]].collectionName, ref.id, 1)
    val sortValue = sort match {
      case SortDirection.ASC => "ASC"
      case SortDirection.DESC => "DESC"
    }
    Query(s"SORT $name.${field.name} $sortValue", Map.empty)
  }
}

case class ForPart[D <: Document[D], Model <: DocumentModel[D]](ref: DocumentRef[D, Model],
                                                                collection: Collection[D]) extends QueryPart {
  override def build(builder: AQLBuilder): Query = {
    val name = builder.ref2Name(ref)
    Query(s"FOR $name IN ${collection.name}", Map.empty)
  }
}

case class FilterPart(filter: Filter) extends QueryPart {
  override def build(builder: AQLBuilder): Query = {
    val query = filter.build(builder)
    query.copy(value = s"FILTER ${query.value}")
  }
}

class Filter(left: AQLBuilder => Query, condition: String, right: AQLBuilder => Query) {
  def &&(filter: Filter): Filter = {
    new Filter(build, "&&", filter.build)
  }

  def build(builder: AQLBuilder): Query = {
    val l = left(builder)
    val r = right(builder)
    Query(s"${l.value} $condition ${r.value}", l.args ++ r.args)
  }
}

case class ReturnPart[D <: Document[D], Model <: DocumentModel[D]](ref: DocumentRef[D, Model]) extends QueryPart {
  override def build(builder: AQLBuilder): Query = {
    val name = builder.ref2Name(ref)
    Query(s"RETURN $name", Map.empty)
  }
}