package com.outr.arango.query

import com.outr.arango.Ref

class RefManager {
  private var counter = 0
  private var map = Map.empty[Int, String]

  def nameFor(ref: Ref): String = ref.refName match {
    case Some(name) => name
    case None => map.get(ref.hashCode()) match {
      case Some(name) => name
      case None =>
        counter += 1
        val name = s"ref$counter"
        map += ref.hashCode() -> name
        name
    }
  }
}