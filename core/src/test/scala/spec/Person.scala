package spec

import com.outr.arango.{Document, Field, Id, Index}
import fabric.{Json, obj}

case class Person(name: String,
                  age: Int,
                  bio: String = "",
                  favoriteNumbers: List[Int] = Nil,
                  extra: Json = obj(),
                  modified: Long = System.currentTimeMillis(),
                  _id: Id[Person] = Person.id()) extends Document[Person]

object Person extends PersonModel {
  override val bio: Field[String] = super.bio.modify(_.reverse, identity)
  override val modified: Field[Long] = super.modified.modified()

  override def indexes: List[Index] = List(name.index.persistent(unique = true))
}