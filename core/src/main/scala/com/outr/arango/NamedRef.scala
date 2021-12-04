package com.outr.arango

case class NamedRef(name: String) extends Ref {
  lazy val refName: Option[String] = Some(name)
}

object NamedRef {
  def apply(): NamedRef = NamedRef(s"$$ref_${Unique(length = 8)}")
}