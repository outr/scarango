package com.outr.arango.util

import cats.effect.IO

import java.util.concurrent.CompletableFuture

import scala.jdk.FutureConverters._

object JavaHelpers {
  implicit class CompletableFutureExtras[T](cf: CompletableFuture[T]) {
    def toIO: IO[T] = IO.fromFuture(IO(cf.asScala))
  }
}