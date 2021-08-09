package com.outr.arango.core

case class CreateOptions(waitForSync: Boolean = false,
                         returnNew: Boolean = false,
                         returnOld: Boolean = false,
                         overwrite: OverwriteMode = OverwriteMode.None,
                         silent: Boolean = true,
                         streamTransactionId: Option[String] = None)

object CreateOptions {
  lazy val Insert: CreateOptions = CreateOptions()
  lazy val Upsert: CreateOptions = CreateOptions(overwrite = OverwriteMode.Replace)
}