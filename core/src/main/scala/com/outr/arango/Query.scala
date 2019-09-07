package com.outr.arango

import com.outr.arango.Value.{BigDecimalValue, BooleanValue, DoubleValue, IntValue, JsonValue, LongValue, SeqBigDecimalValue, SeqBooleanValue, SeqDoubleValue, SeqIntValue, SeqLongValue, SeqStringValue, StringValue}
import io.circe.Json

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
    copy(updated, filteredArgs)
  } else {
    this
  }

  def bindVars: Json = Json.obj(args.toList.map {
    case (key, v) => {
      val argValue: Json = v match {
        case Value.Null => Json.Null
        case StringValue(s) => Json.fromString(s)
        case BooleanValue(b) => Json.fromBoolean(b)
        case IntValue(i) => Json.fromInt(i)
        case LongValue(l) => Json.fromLong(l)
        case DoubleValue(d) => Json.fromDoubleOrNull(d)
        case BigDecimalValue(d) => Json.fromBigDecimal(d)
        case SeqStringValue(l) => Json.fromValues(l.map(Json.fromString))
        case SeqBooleanValue(l) => Json.fromValues(l.map(Json.fromBoolean))
        case SeqIntValue(l) => Json.fromValues(l.map(Json.fromInt))
        case SeqLongValue(l) => Json.fromValues(l.map(Json.fromLong))
        case SeqDoubleValue(l) => Json.fromValues(l.map(Json.fromDoubleOrNull))
        case SeqBigDecimalValue(l) => Json.fromValues(l.map(Json.fromBigDecimal))
        case JsonValue(json) => json
      }
      key -> argValue
    }
  }: _*)
}