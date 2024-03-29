package example

import com.outr.arango.{Document, DocumentModel, Field, Id, Index}
import fabric.rw._

import java.util.Calendar

//case class Person(name: String,
//                  age: Int = 21,
//                  address: Address)
//
//case class Address(line1: String,
//                   line2: String,
//                   city: String,
//                   state: String,
//                   zip: String) extends Foo
//
//case class Test(value: String)
//  extends Foo
//    with Bar {
//  // Internal code
//}

case class JustDoc(name: String) extends Foo with Document[JustDoc] {
  // Custom method
  def doSomething(): Unit = scribe.info("Testing!")
}

object JustDoc {}

/*case class JustDoc(name: String, _id: Id[JustDoc] = JustDoc.id()) extends Document[JustDoc] {
  // Custom method
  def doSomething(): Unit = scribe.info("Testing!")
}

object JustDoc extends DocumentModel[JustDoc] {
  override implicit val rw: RW[JustDoc] = RW.gen
  override val collectionName: String = "JustDoc"

  val name: Field[String] = field("name")

  override def indexes: List[Index] = Nil
}*/

//case class Correct(name: String, _id: Id[Correct] = Correct.id()) extends Document[Correct]
//
//object Correct extends CorrectModel
//
//trait CorrectModel extends DocumentModel[Correct] {
//  override implicit val rw: RW[Correct] = RW.gen
//
//  val name: Field[String] = field("name")
//
//  override val collectionName: String = "correct"
//
//  override def indexes: List[Index] = Nil
//}
//
trait Foo
//
//trait Bar