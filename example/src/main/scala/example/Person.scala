package example

import com.outr.arango.{Document, DocumentModel, Field, Id, Index}
import fabric.rw._

case class Person(name: String,
                  age: Int = 21,
                  address: Address)

case class Address(line1: String,
                   line2: String,
                   city: String,
                   state: String,
                   zip: String) extends Foo

case class Test(value: String)
  extends Foo
    with Bar {
  // Internal code
}

case class Correct(name: String, _id: Id[Correct] = Correct.id()) extends Document[Correct]

object Correct extends CorrectModel {
}

trait CorrectModel extends DocumentModel[Correct] {
  override implicit val rw: RW[Correct] = RW.gen

  val name: Field[String] = field("name")

  override val collectionName: String = "correct"

  override def indexes: List[Index] = Nil
}

trait Foo

trait Bar