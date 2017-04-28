package com.outr.arango

trait Edge {
  def `type`: String
  def _from: String
  def _to: String
}
