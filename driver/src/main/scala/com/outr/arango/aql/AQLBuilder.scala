package com.outr.arango.aql

import java.util.concurrent.atomic.AtomicInteger

import com.outr.arango.{Collection, Document, DocumentModel, DocumentRef, Field, Query, Value}

import scala.annotation.tailrec
import scala.language.implicitConversions

class AQLBuilder(val parts: List[QueryPart] = Nil) {
  private var map = Map.empty[String, Int]
  private var used = Set.empty[String]
  private lazy val incrementor = new AtomicInteger(0)

  private[aql] def ref2Name[D <: Document[D], Model <: DocumentModel[D]](ref: DocumentRef[D, Model]): String = {
    createName(ref.collectionName, ref.id, 1)
  }

  private[aql] def createArg(): String = s"arg${incrementor.incrementAndGet()}"

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
  def UPDATE[D <: Document[D], Model <: DocumentModel[D]](ref: DocumentRef[D, Model], values: FieldAndValue[_]*): AQLBuilder = {
    withPart(UpdatePart(ref, values.toList))
  }
  def SORT[T](f: => (Field[T], SortDirection)): AQLBuilder = withPart(SortPart[T](() => f))
  def RETURN(part: ReturnPart): AQLBuilder = {
    withPart(part)
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
    val name = builder.createName(ref.model.asInstanceOf[DocumentModel[_]].collectionName, ref.id, 1)
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

case class UpdatePart[D <: Document[D], Model <: DocumentModel[D]](ref: DocumentRef[D, Model], values: List[FieldAndValue[_]]) extends QueryPart {
  override def build(builder: AQLBuilder): Query = {
    val name = builder.ref2Name(ref)
    var map = Map.empty[String, Value]
    val data = values.map { fv =>
      val arg = builder.createArg()
      map += arg -> fv.value
      s"${fv.field.name}: @$arg"
    }.mkString(", ")
    Query(s"UPDATE $name WITH {$data} IN ${ref.collectionName}", map)
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

sealed trait ReturnPart extends QueryPart {
  def value(builder: AQLBuilder): String

  override def build(builder: AQLBuilder): Query = {
    Query(s"RETURN ${value(builder)}", Map.empty)
  }
}

case class DocumentRefReturnPart[D <: Document[D], Model <: DocumentModel[D]](ref: DocumentRef[D, Model]) extends ReturnPart {
  override def value(builder: AQLBuilder): String = builder.ref2Name(ref)
}

object NewReturnPart extends ReturnPart {
  override def value(builder: AQLBuilder): String = "NEW"
}

case class FieldAndValue[T](field: Field[T], value: Value)