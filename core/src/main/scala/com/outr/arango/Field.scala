package com.outr.arango

import com.outr.arango.mutation.{DataMutation, ModifyFieldValue}
import com.outr.arango.query.dsl._
import com.outr.arango.query.{Query, QueryPart, SortDirection}
import fabric.{Str, arr}
import fabric.rw._

import scala.concurrent.duration.FiniteDuration

class Field[F](val fieldName: String,
               val isArray: Boolean,
               val mutation: Option[DataMutation])
              (implicit val rw: RW[F], model: Option[DocumentModel[_]]) extends QueryPart.Support {
  def this(fieldName: String, isArray: Boolean)(implicit rw: RW[F], model: Option[DocumentModel[_]]) = {
    this(fieldName, isArray, None)(rw, model)
  }

  def this(fieldName: String)(implicit rw: RW[F], model: Option[DocumentModel[_]]) = {
    this(fieldName, isArray = false)(rw, model)
  }

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

  def field[T: RW](name: String): Field[T] = model match {
    case Some(m) => m.field[T](s"$fieldName.$name")
    case None => throw new RuntimeException("No model defined!")
  }

  def withMutation(mutation: DataMutation): Field[F] = {
    this.mutation.foreach(m => throw new RuntimeException(s"Field $fieldName already has a mutation set: $m"))
    new Field[F](fieldName, isArray, Some(mutation))
  }

  def modify(storage: F => F, retrieval: F => F): Field[F] = withMutation(ModifyFieldValue(this, storage, retrieval))

  def apply(value: F): FieldAndValue[F] = FieldAndValue(this, value.json)

  lazy val opt: Field[Option[F]] = new Field[Option[F]](fieldName, isArray, mutation)

  override def toQueryPart: QueryPart = QueryPart.Static(fieldName)

  private def cond(value: F, condition: String): Filter = {
    val context = QueryBuilderContext()
    val (refOption, f) = withReference(this)
    val ref = refOption.getOrElse(throw new RuntimeException("No reference for field!"))
    val leftName = context.name(ref)
    val left = Query(s"$leftName.${f.fieldName}")
    val right = Query.variable(value.json)

    new Filter(left, condition, right)
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

  def ===(value: F): Filter = {
    cond(value, "==")
  }

  def ===(field: => Field[F]): Filter = {
    cond(field, "==")
  }

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