package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class AdminStatisticsFiguresStruct(cuts: Option[String] = None,
                                        description: Option[String] = None,
                                        group: Option[String] = None,
                                        identifier: Option[String] = None,
                                        name: Option[String] = None,
                                        `type`: Option[String] = None,
                                        units: Option[String] = None)

object AdminStatisticsFiguresStruct {
  implicit val rw: ReaderWriter[AdminStatisticsFiguresStruct] = ccRW
}