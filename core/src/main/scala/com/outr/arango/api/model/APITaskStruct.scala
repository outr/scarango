package com.outr.arango.api.model

import io.circe.Json

/**
  * APITaskStruct
  *
  * @param command the javascript function for this task
  * @param created The timestamp when this task was created
  * @param database the database this task belongs to
  * @param id A string identifying the task
  * @param name The fully qualified name of the user function
  * @param offset time offset in seconds from the created timestamp
  * @param period this task should run each `period` seconds
  * @param type What type of task is this [ `periodic`, `timed`]
  *         - periodic are tasks that repeat periodically
  *         - timed are tasks that execute once at a specific time
  *
  * WARNING: This code is generated by youi-plugin's generateHttpClient. Do not modify directly.
  */
case class APITaskStruct(command: Option[String] = None,
                         created: Option[Double] = None,
                         database: Option[String] = None,
                         id: Option[String] = None,
                         name: Option[String] = None,
                         offset: Option[Double] = None,
                         period: Option[Double] = None,
                         `type`: Option[String] = None)