package com.outr.arango

case class Query(value: String, args: Map[String, Value]) {
  def fixed(): Query = if (args.valuesIterator.contains(Value.Null)) {
    var updated = value
    val filteredArgs = args.filter {
      case (k, v) => if (v == Value.Null) {
        updated = updated.replaceAllLiterally(s"@$k", "null")
        false
      } else {
        true
      }
    }
    scribe.info(s"From: $value, To: $updated")
    scribe.info(s"Args: ${args.keySet}, To: ${filteredArgs.keySet}")
    copy(updated, filteredArgs)
  } else {
    this
  }
}