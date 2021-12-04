package com.outr.arango.core

case class KeyOptions(allowUserKeys: Boolean, `type`: KeyType, increment: Option[Int], offset: Option[Int])
