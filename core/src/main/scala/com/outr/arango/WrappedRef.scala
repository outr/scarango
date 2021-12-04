package com.outr.arango

class WrappedRef[T](val wrapped: T, val refName: Option[String] = None) extends Ref