package com.outr.arango

import scala.language.experimental.macros
import scala.language.implicitConversions

package object query {
  private var forced = false

  implicit class AQLInterpolator(val sc: StringContext) extends AnyVal {
    /**
      * AQL interpolation with compile-time validation against ArangoDB
      */
    def aql(args: Any*): Query = macro AQLMacros.aql

    /**
      * AQL interpolation unvalidated.
      *
      * WARNING: This can lead to runtime errors if you use invalid AQL. Ideally, this should only be used for partial
      * queries.
      */
    def aqlu(args: Any*): Query = macro AQLMacros.aqlu
  }

  implicit def ref2ReturnPart(ref: Ref): ReturnPart = {
    ReturnPart.RefReturn(ref)
  }

  implicit def string2ReturnPart(json: String): ReturnPart = this.json(json)

  implicit class FieldExtras[T](field: => Field[T]) {
    def thisField: Field[T] = field

    def apply(value: T): FieldAndValue[T] = macro AQLMacros.fieldAndValue
    private def cond(value: T, condition: String)(implicit conversion: T => Value): Filter = {
      val context = QueryBuilderContext()
      val (refOption, f) = withReference(field)
      val ref = refOption.getOrElse(throw new RuntimeException("No reference for field!"))
      val leftName = context.name(ref)
      val rightName = context.createArg
      val left = Query(s"$leftName.${f.fieldName}", Map.empty)
      val right = Query(s"@$rightName", Map(rightName -> conversion(value)))

      new Filter(left, condition, right)
    }
    private def cond(values: Seq[T], condition: String)(implicit conversion: T => Value): Filter = {
      val context = QueryBuilderContext()
      val (refOption, f) = withReference(field)
      val ref = refOption.getOrElse(throw new RuntimeException("No reference for field!"))
      val leftName = context.name(ref)
      val rightName = context.createArg
      val left = Query(s"$leftName.${f.fieldName}", Map.empty)
      val right = Query(s"@$rightName", Map(rightName -> Value.values(values.map(conversion))))

      new Filter(left, condition, right)
    }
    private def stringCond(value: String, condition: String): Filter = {
      val context = QueryBuilderContext()
      val (refOption, f) = withReference(field)
      val ref = refOption.getOrElse(throw new RuntimeException("No reference for field!"))
      val leftName = context.name(ref)
      val rightName = context.createArg
      val left = Query(s"$leftName.${f.fieldName}", Map.empty)
      val right = Query(s"@$rightName", Map(rightName -> Value.string(value)))

      new Filter(left, condition, right)
    }
    def is(value: T)(implicit conversion: T => Value): Filter = ===(value)
    def ===(value: T)(implicit conversion: T => Value): Filter = {
      cond(value, "==")
    }
    def isNot(value: T)(implicit conversion: T => Value): Filter = !==(value)
    def !=(value: T)(implicit conversion: T => Value): Filter = !==(value)
    def !==(value: T)(implicit conversion: T => Value): Filter = {
      cond(value, "!=")
    }
    def >(value: T)(implicit conversion: T => Value): Filter = {
      cond(value, ">")
    }
    def >=(value: T)(implicit conversion: T => Value): Filter = {
      cond(value, ">=")
    }
    def <(value: T)(implicit conversion: T => Value): Filter = {
      cond(value, "<")
    }
    def <=(value: T)(implicit conversion: T => Value): Filter = {
      cond(value, "<=")
    }
    def IN(values: Seq[T])(implicit conversion: T => Value): Filter = {
      cond(values, "IN")
    }
    def NOT_IN(values: Seq[T])(implicit conversion: T => Value): Filter = {
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

    def asc: (Field[T], SortDirection) = (field, SortDirection.ASC)
    def ASC: (Field[T], SortDirection) = (field, SortDirection.ASC)
    def desc: (Field[T], SortDirection) = (field, SortDirection.DESC)
    def DESC: (Field[T], SortDirection) = (field, SortDirection.DESC)
  }

  def withReference[Return](ref: Ref)(f: => Return): Return = {
    val context = QueryBuilderContext()
    context.ref = Some(ref)
    forced = true
    try {
      val r: Return = f
      r
    } finally {
      context.ref = None
      forced = false
    }
  }

  def withReference[Return](f: => Return): (Option[Ref], Return) = {
    val context = QueryBuilderContext()
    if (!forced) context.ref = None
    try {
      val r = f
      (context.ref, r)
    } finally {
      context.ref = None
    }
  }

  implicit def ref2Wrapped[T](ref: WrappedRef[T]): T = {
    val context = QueryBuilderContext()
    context.ref = Some(ref)
    ref.wrapped
  }

  def FOR[D <: Document[D], Model <: DocumentModel[D]](ref: DocumentRef[D, Model]): ForPartial[D, Model] = ForPartial(ref)

  def REMOVE[D <: Document[D], Model <: DocumentModel[D]](ref: DocumentRef[D, Model]): RemovePartial[D, Model] = RemovePartial(ref)

  def SORT[T](f: => (Field[T], SortDirection)): Unit = {
    val context = QueryBuilderContext()
    val (refOption, (field, sort)) = withReference(f)
    val ref = refOption.getOrElse(throw new RuntimeException("No ref option found for SORT!"))
    val name = context.name(ref)
    val sortValue = sort match {
      case SortDirection.ASC => "ASC"
      case SortDirection.DESC => "DESC"
    }
    context.addQuery(Query(s"SORT $name.${field.fieldName} $sortValue", Map.empty))
  }

  def FILTER(filter: Filter): Unit = {
    val query = filter.build()
    addQuery(query.copy(value = s"FILTER ${query.value}"))
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

  def LIMIT(offset: Int, limit: Int): Unit = addQuery(Query(s"LIMIT $offset, $limit", Map.empty))

  def RETURN(part: ReturnPart): Unit = addQuery(part.build())

  def addQuery(query: Query): Unit = {
    val context = QueryBuilderContext()
    context.addQuery(query)
  }

  def ref: Ref = new Ref {
    override def refName: Option[String] = None
  }
  def ref(name: String): Ref = NamedRef(name)
  def ref[T](wrapped: T, name: Option[String]): WrappedRef[T] = new WrappedRef[T](wrapped, name)

  def aql(f: => Unit): Query = QueryBuilderContext.contextualize(f)
}