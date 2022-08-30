package com.outr.arango

import com.outr.arango.core.CreateCollectionOptions
import com.outr.arango.mutation.{DataMutation, IdMutation, ModifyFieldValue}
import fabric.rw.ReaderWriter

trait DocumentModel[D <: Document[D]] { model =>
  protected implicit val modelOption: Option[DocumentModel[D]] = Some(model)

  val collectionName: String

  private var _fields = List.empty[Field[_]]
  def fields: List[Field[_]] = _fields

  implicit val rw: ReaderWriter[D]

  val _id: Field[Id[D]] = field("_id", IdMutation)

  protected def generateId(): String = Unique()

  private[arango] def defineField[T](field: Field[T]): Field[T] = synchronized {
    _fields = _fields.filterNot(_.fieldName == field.fieldName) ::: List(field)
    field
  }

  protected[arango] def field[T: ReaderWriter](name: String, mutation: Option[DataMutation]): Field[T] =
    new Field[T](name, mutation)
  protected[arango] def field[T: ReaderWriter](name: String): Field[T] = field[T](name, None)
  protected[arango] def field[T: ReaderWriter](name: String, mutation: DataMutation): Field[T] = field[T](name, Some(mutation))

  object index {
    def apply(fields: Field[_]*): List[Index] = fields.map(_.index.persistent()).toList
    def unique(fields: Field[_]*): List[Index] = fields.map(_.index.persistent(unique = true)).toList
  }

  def indexes: List[Index]

  def mutations: List[DataMutation] = Nil

  final def allMutations: List[DataMutation] = fields.flatMap(_.mutation) ::: mutations

  def collectionOptions: CreateCollectionOptions = CreateCollectionOptions()

  def id(value: String = generateId()): Id[D] = {
    val index = value.indexOf('/')
    val v = if (index != -1) {
      value.substring(index + 1)
    } else {
      value
    }
    Id[D](v, collectionName)
  }
}