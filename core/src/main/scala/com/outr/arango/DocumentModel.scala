package com.outr.arango

import com.outr.arango.core.{CollectionSchema, ComputeOn, ComputedValue, CreateCollectionOptions}
import com.outr.arango.mutation.{DataMutation, IdMutation}
import fabric.rw.RW

trait DocumentModel[D <: Document[D]] { model =>
  protected implicit val modelOption: Option[DocumentModel[D]] = Some(model)

  val collectionName: String

  def waitForSync: Option[Boolean] = None
  def schema: Option[CollectionSchema] = None

  protected def computedValues: List[ComputedValue] = Nil

  final def allComputedValues: List[ComputedValue] = computedValues ::: fields.flatMap(_.computedValues)

  private var _fields = List.empty[Field[_]]

  def fields: List[Field[_]] = _fields

  implicit val rw: RW[D]

  val _id: Field[Id[D]] = field("_id", mutation = Some(IdMutation))

  protected def generateId(): String = Unique()

  private[arango] def defineField[T](field: Field[T]): Field[T] = synchronized {
    _fields = _fields.filterNot(_.fieldName == field.fieldName) ::: List(field)
    field
  }

  protected[arango] def field[T](name: String,
                                 mutation: Option[DataMutation] = None)
                                (implicit rw: RW[T], parent: Option[Field[_]] = None): Field[T] =
    new Field[T](
      fieldName = name,
      container = false,
      mutation = mutation)(rw, Some(this), parent)

  object index {
    def apply(fields: Field[_]*): List[Index] = fields.map(_.index.persistent()).toList

    def unique(fields: Field[_]*): List[Index] = fields.map(_.index.persistent(unique = true)).toList
  }

  def indexes: List[Index]

  def mutations: List[DataMutation] = Nil

  final def allMutations: List[DataMutation] = fields.flatMap(_.mutation) ::: mutations

  def collectionOptions: CreateCollectionOptions = CreateCollectionOptions()

  def id(value: String = generateId()): Id[D] = Id.parse[D](value, collectionName)
}