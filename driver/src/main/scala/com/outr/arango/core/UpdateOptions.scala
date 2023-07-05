package com.outr.arango.core

case class UpdateOptions(keepNull: Boolean = false,
                         mergeObjects: Boolean = true,
                         waitForSync: Boolean = false,
                         ignoreRevs: Boolean = true,
                         ifMatch: Option[String] = None,
                         returnNew: Boolean = false,
                         returnOld: Boolean = false,
                         silent: Boolean = true,
                         streamTransaction: Option[StreamTransaction] = None)

object UpdateOptions {
  lazy val Default: UpdateOptions = UpdateOptions()
}