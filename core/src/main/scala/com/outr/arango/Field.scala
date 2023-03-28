package com.outr.arango

import com.outr.arango.mutation.{DataMutation, ModifyFieldValue}
import com.outr.arango.query.dsl._
import com.outr.arango.query.{Query, QueryPart, SortDirection}
import fabric.rw._
import fabric.{Json, Str, arr}

import scala.concurrent.duration.FiniteDuration

class Field[F](val fieldName: String,
               val isArray: Boolean = false,
               val mutation: Option[DataMutation] = None)
              (implicit val rw: RW[F],
               model: Option[DocumentModel[_]],
               private val _parent: Option[Field[_]] = None) extends QueryPart.Support {
  protected implicit def thisField: Option[Field[_]] = Some(this)

  lazy val fullyQualifiedName: String = {
    val name = if (isArray) s"$fieldName[*]" else fieldName
    parent match {
      case Some(p) => s"${p.fullyQualifiedName}.$name"
      case None => name
    }
  }

  lazy val depth: Int = parent match {
    case Some(p) => p.depth + 1
    case None => 0
  }

  lazy val arrayDepth: Int = {
    val v = if (isArray) 1 else 0
    parent match {
      case Some(p) => p.arrayDepth + v
      case None => v
    }
  }

  lazy val fullyQualifiedFilter: String = {
    val filter = if (isArray) {
      s"$fieldName[? FILTER CURRENT"
    } else {
      fieldName
    }
    parent match {
      case Some(p) => s"${p.fullyQualifiedFilter}.$filter"
      case None => filter
    }
  }

  def parent: Option[Field[_]] = _parent

  model.foreach(_.defineField(this))

  object index {
    def persistent(sparse: Boolean = false,
                   unique: Boolean = false): Index = {
      Index(IndexType.Persistent, List(fieldName), sparse, unique)
    }

    def geo(geoJson: Boolean = true): Index = {
      Index(IndexType.Geo, List(fieldName), geoJson = geoJson)
    }

    def ttl(expireAfter: FiniteDuration): Index = {
      val seconds = expireAfter.toSeconds.toInt
      Index(IndexType.TTL, List(fieldName), expireAfterSeconds = seconds)
    }
  }

//  def field[T: RW](name: String): Field[T] = model match {
//    case Some(m) => m.field[T](s"$fieldName.$name")
//    case None => throw new RuntimeException("No model defined!")
//  }

  def withMutation(mutation: DataMutation): Field[F] = {
    this.mutation.foreach(m => throw new RuntimeException(s"Field $fieldName already has a mutation set: $m"))
    new Field[F](fieldName, isArray, Some(mutation))(rw, model, parent)
  }

  def modify(storage: F => F, retrieval: F => F): Field[F] = withMutation(ModifyFieldValue(this, storage, retrieval))

  def apply(value: F): FieldAndValue[F] = FieldAndValue(this, value.json)

  lazy val opt: Field[Option[F]] = new Field[Option[F]](fieldName, isArray, mutation)(implicitly[RW[Option[F]]], model, parent)

  override def toQueryPart: QueryPart = QueryPart.Static(fullyQualifiedName)

  private def cond[T: RW](value: T, condition: String): Filter = {
    val context = QueryBuilderContext()
    val (refOption, f) = withReference(this)
    val ref = refOption.getOrElse(throw new RuntimeException("No reference for field!"))
    val leftName = context.name(ref)
    val left = Query(s"$leftName.${f.fullyQualifiedFilter}")
    val right = Query.variable(value.json)
    val arrayClosures = (0 until arrayDepth).toList.map(_ => QueryPart.Static("]"))

    new Filter(left, condition, right.withParts(arrayClosures: _*))
  }

  private def cond(that: => Field[F], condition: String): Filter = {
    val context = QueryBuilderContext()
    val (leftRefOption, leftField) = withReference(this)
    val leftRef = leftRefOption.getOrElse(throw new RuntimeException("No reference for field!"))
    val leftName = context.name(leftRef)
    val left = Query(s"$leftName.${leftField.fieldName}")
    val (rightRefOption, rightField) = withReference(that)
    val rightRef = rightRefOption.getOrElse(throw new RuntimeException("No reference for field!"))
    val rightName = context.name(rightRef)
    val right = Query(s"$rightName.${rightField.fieldName}")

    new Filter(left, condition, right)
  }

  private def cond(values: Seq[F], condition: String): Filter = {
    val context = QueryBuilderContext()
    val (refOption, f) = withReference(this)
    val ref = refOption.getOrElse(throw new RuntimeException("No reference for field!"))
    val leftName = context.name(ref)
    val left = Query(s"$leftName.${fieldName}")
    val right = Query.variable(arr(values.map(_.json): _*))

    new Filter(left, condition, right)
  }

  private def stringCond(value: String, condition: String): Filter = {
    val context = QueryBuilderContext()
    val (refOption, f) = withReference(this)
    val ref = refOption.getOrElse(throw new RuntimeException("No reference for field!"))
    val leftName = context.name(ref)
    val left = Query(s"$leftName.${f.fieldName}")
    val right = Query.variable(Str(value))

    new Filter(left, condition, right)
  }

  def is(value: F): Filter = ===(value)

  def is[T: RW](value: T): Filter = cond(value, "==")

  def ===(value: F): Filter = {
    cond(value, "==")
  }

  def ===(field: => Field[F]): Filter = {
    cond(field, "==")
  }

  def ===[T: RW](value: T): Filter = cond(value, "==")

  def isNot(value: F): Filter = !==(value)

  def !=(value: F): Filter = !==(value)

  def !==(value: F): Filter = {
    cond(value, "!=")
  }

  def >(value: F): Filter = {
    cond(value, ">")
  }

  def >=(value: F): Filter = {
    cond(value, ">=")
  }

  def <(value: F): Filter = {
    cond(value, "<")
  }

  def <=(value: F): Filter = {
    cond(value, "<=")
  }

  def IN(values: Seq[F]): Filter = {
    cond(values, "IN")
  }

  def NOT_IN(values: Seq[F]): Filter = {
    cond(values, "NOT IN")
  }

  def LIKE(value: String): Filter = {
    stringCond(value, "LIKE")
  }

  def NOT_LIKE(value: String): Filter = {
    stringCond(value, "NOT LIKE")
  }

  def =~(value: String): Filter = {
    stringCond(value, "=~")
  }

  def !~(value: String): Filter = {
    stringCond(value, "!~")
  }

  def asc: (Field[F], SortDirection) = (this, SortDirection.ASC)

  def ASC: (Field[F], SortDirection) = (this, SortDirection.ASC)

  def desc: (Field[F], SortDirection) = (this, SortDirection.DESC)

  def DESC: (Field[F], SortDirection) = (this, SortDirection.DESC)
}