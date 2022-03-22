package com.outr.arango.maintenance

trait MaintenanceTaskInstance {
  def name: String

  def status: TaskStatus

  def cancel(): Unit
}
