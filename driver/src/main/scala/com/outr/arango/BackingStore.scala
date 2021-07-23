package com.outr.arango
import fabric.rw.{ReaderWriter, ccRW}

case class BackingStore(data: fabric.Value, _id: Id[BackingStore]) extends Document[BackingStore]

object BackingStore extends DocumentModel[BackingStore] {
  override implicit val rw: ReaderWriter[BackingStore] = ccRW

  override def indexes: List[Index] = Nil

  override val collectionName: String = "backingStore"
}