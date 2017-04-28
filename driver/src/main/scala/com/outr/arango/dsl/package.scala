package com.outr.arango

package object dsl {
  def FOR(variableNames: String*): PartialFor = new PartialFor(variableNames.toList)

  implicit class PartialCondition(left: Expression) {

  }
}