package com.outr.arango

import com.outr.arango.core.{ComputeOn, ComputedValue}
import com.outr.arango.mutation.{DataMutation, ModifyFieldValue}
import com.outr.arango.query.dsl._
import com.outr.arango.query.{Query, QueryPart, SortDirection}
import fabric.define.DefType
import fabric.rw._
import fabric.{Str, arr}

import scala.concurrent.duration.FiniteDuration

class Field[F](val fieldName: String,
               val container: Boolean = true,
               val mutation: Option[DataMutation] = None,
               val computedValues: List[ComputedValue] = Nil)
              (implicit val rw: RW[F],
               model: Option[DocumentModel[_]],
               private val _parent: Option[Field[_]] = None) extends QueryPart.Support {
  protected val isArray: Boolean = rw.definition match {
    case DefType.Arr(_) => true
    case DefType.Opt(DefType.Arr(_)) => true
    case _ => false
  }
  protected implicit def thisField: Option[Field[_]] = Some(this)

  lazy val fullyQualifiedName: String = fqn(true)

  protected def fqn(top: Boolean): String = {
    def asterisk: String = if (top) "**" else "*"
    val name = if (isArray && container) s"$fieldName[$asterisk]" else fieldName
    parent match {
      case Some(p) => s"${p.fqn(false)}.$name"
      case None => name
    }
  }

  lazy val depth: Int = parent match {
    case Some(p) => p.depth + 1
    case None => 0
  }

  lazy val arrayDepth: Int = {
    val v = if (isArray && container) 1 else 0
    parent match {
      case Some(p) => p.arrayDepth + v
      case None => v
    }
  }

  lazy val fullyQualifiedFilter: String = {
    val filter = if (isArray && container) {
      s"$fieldName[? FILTER CURRENT"
    } else {
      fieldName
    }
    parent match {
      case Some(p) => s"${p.fullyQualifiedFilter}.$filter"
      case None => filter
    }
  }

  lazy val fullyQualifiedFilterContains: String = {
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

  private def refPart(name: Option[String]): QueryPart = {
    val ref = useRef()
    name match {
      case Some(n) => QueryPart.Ref(ref).withPart(QueryPart.Static(s".$n"))
      case None => QueryPart.Ref(ref)
    }
  }

  def fqnPart: QueryPart = refPart(Some(fullyQualifiedName))
  def fqfPart: QueryPart = refPart(Some(fullyQualifiedFilter))
  def fqfcPart: QueryPart = refPart(Some(fullyQualifiedFilterContains))

  def parent: Option[Field[_]] = _parent

  model.foreach(_.defineField(this))

  object index {
    def persistent(sparse: Boolean = false,
                   unique: Boolean = false): Index = Index.Persistent(
      fields = List(fieldName),
      sparse = sparse,
      unique = unique
    )

    def geo(geoJson: Boolean = true): Index = Index.Geo(
      fields = List(fieldName),
      geoJson = geoJson
    )

    def ttl(expireAfter: FiniteDuration): Index = Index.TTL(
      fields = List(fieldName),
      expireAfterSeconds = expireAfter.toSeconds.toInt
    )
  }

  def withMutation(mutation: DataMutation): Field[F] = {
    this.mutation.foreach(m => throw new RuntimeException(s"Field $fieldName already has a mutation set: $m"))
    new Field[F](
      fieldName = fieldName,
      container = container,
      mutation = Some(mutation),
      computedValues = computedValues
    )(rw, model, parent)
  }

  def modify(storage: F => F, retrieval: F => F): Field[F] = withMutation(ModifyFieldValue(this, storage, retrieval))

  def computed(expression: String,
               computeOn: Set[ComputeOn],
               overwrite: Boolean = true,
               keepNull: Boolean = false,
               failOnWarning: Boolean = false): Field[F] = new Field[F](
    fieldName = fieldName,
    container = container,
    mutation = mutation,
    computedValues = computedValues ::: List(ComputedValue(
      name = fieldName,
      expression = expression,
      overwrite = overwrite,
      computeOn = computeOn,
      keepNull = keepNull,
      failOnWarning = failOnWarning
    ))
  )(rw, model, parent)

  def modified(): Field[F] = computed(
    expression = "RETURN DATE_NOW()",
    computeOn = Set(ComputeOn.Replace, ComputeOn.Update)
  )

  def apply(value: F): FieldAndValue[F] = FieldAndValue(this, value.json)

  lazy val opt: Field[Option[F]] = new Field[Option[F]](fieldName, container, mutation)(implicitly[RW[Option[F]]], model, parent)

  override def toQueryPart: QueryPart = QueryPart.Static(fullyQualifiedName)

  private def cond[T: RW](value: T, condition: String): Filter = {
    val left = Query(List(fqfPart))
    val right = Query.variable(value.json)
    val arrayClosures = (0 until arrayDepth).toList.map(_ => QueryPart.Static("]"))

    new Filter(left, condition, right.withParts(arrayClosures: _*))
  }

  private def cond(that: => Field[_], condition: String): Filter = {
    val left = Query(List(fqfPart))
    val right = Query(List(that.fqfPart))

    new Filter(left, condition, right)
  }

  private def cond(values: Seq[F], condition: String): Filter = {
    val left = Query(List(fqnPart))
    val right = Query.variable(arr(values.map(_.json): _*))

    new Filter(left, condition, right)
  }

  private def stringCond(value: String, condition: String): Filter = {
    val left = Query(List(fqnPart))
    val right = Query.variable(Str(value))

    new Filter(left, condition, right)
  }

  def is(value: F): Filter = ===(value)

  def is[T: RW](value: T): Filter = cond(value, "==")

  def is(field: Field[F]): Filter = cond(field, "==")

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

  def IN(field: Field[List[F]]): Filter = {
    cond(field, "IN")
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