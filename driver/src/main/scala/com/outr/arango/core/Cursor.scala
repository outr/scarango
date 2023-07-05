package com.outr.arango.core

import fabric.Json

case class Cursor[T](id: String,
                     nextBatchId: String,
                     iterator: Iterator[Json],
                     converter: Json => T)