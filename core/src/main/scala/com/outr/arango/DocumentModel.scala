package com.outr.arango

import com.outr.arango.core.CreateCollectionOptions
import fabric.rw.ReaderWriter

trait DocumentModel[D <: Document[D]] { model =>
  val collectionName: String

  private var _fields = List.empty[Field[_]]
  def fields: List[Field[_]] = _fields

  implicit val rw: ReaderWriter[D]

  implicit class FieldExtras[F](field: Field[F]) {
    def field[T](name: String): Field[T] = model.field[T](s"${field.name}.$name")
  }

  val _id: Field[Id[D]] = field("_id", IdMutation)

  protected def generateId(): String = Unique()

  protected def field[T](name: String, mutation: Option[DataMutation]): Field[T] = synchronized {
    val field = Field[T](name, mutation)
    _fields = _fields ::: List(field)
    field
  }
  protected def field[T](name: String): Field[T] = field[T](name, None)
  protected def field[T](name: String, mutation: DataMutation): Field[T] = field[T](name, Some(mutation))

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