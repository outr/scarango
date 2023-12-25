package spec

import com.outr.arango.{DocumentModel, Field}
import fabric.Json
import fabric.rw._

// TODO: Support SBT pre-compile generation
trait PersonModel extends DocumentModel[Person] {
  override implicit val rw: RW[Person] = RW.gen

  def name: Field[String] = field("name")
  def age: Field[Int] = field("age")
  def bio: Field[String] = field[String]("bio")
  def favoriteNumbers: Field[List[Int]] = field("favoriteNumbers")
  def extra: Field[Json] = field("extra")
  def modified: Field[Long] = field[Long]("modified")

  override val collectionName: String = "person"
}
