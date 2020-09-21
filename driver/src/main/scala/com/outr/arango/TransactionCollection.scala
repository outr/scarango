package com.outr.arango

import com.outr.arango.transaction.Transaction

class TransactionCollection[D <: Document[D]](override val collection: Collection[D], val currentTransaction: Transaction) extends WrappedCollection[D] {
  override def transaction: Option[Transaction] = Some(currentTransaction)

  override protected def addCollection(): Unit = {
    // Don't auto-add
  }
}