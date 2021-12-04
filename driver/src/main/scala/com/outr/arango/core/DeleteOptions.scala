package com.outr.arango.core

case class DeleteOptions(waitForSync: Boolean = false,
                         ifMatch: Option[String] = None,
                         returnOld: Boolean = false,
                         silent: Boolean = true,
                         streamTransaction: Option[StreamTransaction] = None)

object DeleteOptions {
  lazy val Default: DeleteOptions = DeleteOptions()
}