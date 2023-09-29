package com.outr.arango.core

import fabric.Json

case class CollectionSchema(rule: Option[Json] = None, level: Option[Level] = None, message: Option[String] = None)