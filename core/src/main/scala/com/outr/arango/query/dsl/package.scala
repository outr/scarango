package com.outr.arango.query

import com.outr.arango.{Document, DocumentModel, DocumentRef, Field, FieldAndValue, Id, Ref, WrappedRef}
import fabric._
import fabric.rw._

import scala.language.implicitConversions

package object dsl {
  implicit class DocumentModelExtras[D <: Document[D], M <: DocumentModel[D]](model: M) {
    def ref: DocumentRef[D, M] = DocumentRef[D, M](model, None)
    def ref(name: String): DocumentRef[D, M] = DocumentRef(model, Some(name))
  }

  private val refs = new ThreadLocal[List[Ref]] {
    override def initialValue(): List[Ref] = Nil
  }

  private val consumeRefs = new ThreadLocal[Boolean] {
    override def initialValue(): Boolean = true
  }

  def noConsumingRefs[Return](f: => Return): Return = {
    val previous = consumeRefs.get()
    consumeRefs.set(false)
    try {
      f
    } finally {
      consumeRefs.set(previous)
    }
  }

  def addRef(ref: Ref): Unit = {
    val list = ref :: refs.get()
    refs.set(list)
  }

  def useRef(): Ref = useRefOpt().getOrElse(throw new RuntimeException("No ref found!"))

  def useRefOpt(): Option[Ref] = {
    val ref = refs.get().lastOption
    if (consumeRefs.get() && ref.nonEmpty) {
      refs.set(refs.get().init)
    }
    ref
  }

  def clearRefs(): Unit = refs.set(Nil)

  implicit def ref2ReturnPart(ref: Ref): ReturnPart = {
    ReturnPart.RefReturn(ref)
  }

  implicit def string2ReturnPart(json: String): ReturnPart = this.json(json)

  implicit def fieldsAndQuery2Query[T](faq: (Field[List[T]], Query)): Query = faq._2

  implicit class ValueExtras[T](value: T) {
    def IN(field: Field[T]): Filter = {
      val (ref, f) = withReference(field)
      val query = Query(List(
        QueryPart.Ref(ref),
        QueryPart.Static(s".${f.fieldName}")
      ))
      new Filter(Query(List(QueryPart.Variable(value.json(field.rw)))), "IN", query)
    }
  }

  implicit class NumericFieldExtras[N: Numeric](field: Field[N]) {
    private def toJson(value: N): Json = value match {
      case i: Int => num(i)
      case l: Long => num(l)
      case f: Float => num(f.toDouble)
      case d: Double => num(d)
      case d: BigDecimal => num(d)
      case _ => throw new RuntimeException(s"Unsupported Numeric type: $value (${value.getClass.getName})")
    }

    private def mod(operator: String, right: QueryPart): (Field[N], Query) = {
      val left = field.fqnPart
      val middle = QueryPart.Static(s" $operator ")
      field -> Query(List(left, middle, right))
    }

    def +(value: N): (Field[N], Query) = mod("+", QueryPart.Variable(toJson(value)))
    def -(value: N): (Field[N], Query) = mod("-", QueryPart.Variable(toJson(value)))
    def *(value: N): (Field[N], Query) = mod("*", QueryPart.Variable(toJson(value)))
    def /(value: N): (Field[N], Query) = mod("/", QueryPart.Variable(toJson(value)))
    def %(value: N): (Field[N], Query) = mod("%", QueryPart.Variable(toJson(value)))
  }

  implicit class ListFieldExtras[T: RW](field: Field[List[T]]) {
    def contains(value: T): Filter = {
      val left = Query(List(field.fqfcPart))
      val right = Query.variable(value.json)
      val arrayClosures = (0 until field.arrayDepth + 1).toList.map(_ => QueryPart.Static("]"))

      new Filter(left, "==", right.withParts(arrayClosures: _*))
    }
  }

  implicit def fieldAndValue2FieldAndQuery[T](fieldAndValue: FieldAndValue[T]): (Field[T], Query) = {
    (fieldAndValue.field, Query.variable(fieldAndValue.value))
  }

  def APPEND[T](field: Field[List[T]], values: List[T]): (Field[List[T]], Query) = {
    (field, Query(List(
      QueryPart.Static("APPEND("),
      field.fqnPart,
      QueryPart.Static(", "),
      QueryPart.Variable(values.json(field.rw)),
      QueryPart.Static(")")
    )))
  }

  def PUSH[T: RW](field: Field[List[T]], value: T, unique: Boolean = false): (Field[List[T]], Query) = {
    (field, Query(List(
      QueryPart.Static("PUSH("),
      field.fqnPart,
      QueryPart.Static(", "),
      QueryPart.Variable(value.json),
      QueryPart.Static(", "),
      QueryPart.Static(unique.toString),
      QueryPart.Static(")")
    )))
  }

  def withReference[Return](ref: Ref)(f: => Return): Return = {
    addRef(ref)
    f
  }

  def withReference[Return](f: => Return): (Ref, Return) = {
    val r: Return = f
    (useRef(), r)
  }

  implicit def ref2Wrapped[T](ref: WrappedRef[T]): T = {
    addRef(ref)
    ref.wrapped
  }

  def DOCUMENT[T](id: Id[T]): Query = Query(List(
    QueryPart.Static("DOCUMENT("),
    QueryPart.Variable(id),
    QueryPart.Static(")")
  ))

  def LET(ref: Ref): LetPartial = LetPartial(ref)

  def FOR[D <: Document[D], Model <: DocumentModel[D]](ref: DocumentRef[D, Model]): ForPartial[D, Model] = ForPartial(ref)

  def REMOVE[D <: Document[D], Model <: DocumentModel[D]](ref: DocumentRef[D, Model]): RemovePartial[D, Model] = RemovePartial(ref)

  def SORT[T](f: => (Field[T], SortDirection)): Unit = {
    val context = QueryBuilderContext()
    val (ref, (field, sort)) = withReference(f)
    val sortValue = sort match {
      case SortDirection.ASC => "ASC"
      case SortDirection.DESC => "DESC"
    }
    context.addQuery(Query(List(
      QueryPart.Static("SORT "),
      QueryPart.Ref(ref),
      QueryPart.Static(s".${field.fieldName} $sortValue")
    )))
  }

  def FILTER(filter: Filter): Unit = {
    val query = filter.build()
    addQuery(Query.merge(List(Query("FILTER"), query), " "))
  }

  def COLLECT: CollectStart.type = CollectStart

  def COUNT: CollectWith.Count.type = CollectWith.Count

  def UPDATE[D <: Document[D], Model <: DocumentModel[D]](ref: DocumentRef[D, Model], values: FieldAndValue[_]*): Unit = {
    addQuery(UpdatePart(ref, values.toList).build())
  }

  def NEW: ReturnPart = ReturnPart.New

  def mapped(mappings: (String, Field[_])*): ReturnPart = json(mappings.map {
    case (name, field) => s"$name: ${field.fieldName}"
  }.mkString("{", ", ", "}"))

  def json(json: String): ReturnPart = ReturnPart.Json(json)

  def LIMIT(offset: Int, limit: Int): Unit = addQuery(Query(s"LIMIT $offset, $limit"))

  def LIMIT(limit: Int): Unit = LIMIT(0, limit)

  def RETURN(part: ReturnPart): Unit = addQuery(part.build())

  def RETURN[T](field: => Field[T]): Unit = {
    val (ref, f) = withReference(field)
    addQuery(Query(List(
      QueryPart.Static("RETURN "),
      QueryPart.Ref(ref),
      QueryPart.Static(s".${f.fieldName}")
    )))
  }

  def addQuery(query: Query): Unit = {
    val context = QueryBuilderContext()
    context.addQuery(query)
  }

//  def ref(name: String): Ref = NamedRef(name)
//
//  def ref[T](wrapped: T, name: Option[String]): WrappedRef[T] = new WrappedRef[T](wrapped, name)

  def aql(f: => Unit): Query = QueryBuilderContext.contextualize(f)
}