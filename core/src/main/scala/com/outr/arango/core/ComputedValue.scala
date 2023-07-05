package com.outr.arango.core

case class ComputedValue(name: String,
                         expression: String,
                         overwrite: Boolean,
                         computeOn: ComputeOn,
                         keepNull: Boolean,
                         failOnWarning: Boolean)
