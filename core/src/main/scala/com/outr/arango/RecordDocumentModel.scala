package com.outr.arango

import com.outr.arango.core.{ComputeOn, ComputedValue}
import fabric.rw._

trait RecordDocumentModel[D <: RecordDocument[D]] extends DocumentModel[D] {
  override final val collectionName: String = getClass.getSimpleName.replace("$", "")

  val created: Field[Long] = field("created")
  val modified: Field[Long] = field("modified")

  override protected def computedValues: List[ComputedValue] = List(
    ComputedValue(
      name = modified.fieldName,
      expression = "RETURN DATE_NOW()",
      overwrite = true,
      computeOn = Set(ComputeOn.Update, ComputeOn.Replace),
      keepNull = false,
      failOnWarning = false
    )
  )

  override def indexes: List[Index] = List(
    created.index.persistent(), modified.index.persistent()
  )
}
