package com.outr.arango.maintenance

trait MaintenanceTask {
  def status: TaskStatus

  def cancel(): Unit
}
