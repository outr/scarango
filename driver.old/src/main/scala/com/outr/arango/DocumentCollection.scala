package com.outr.arango

import com.outr.arango.transaction.Transaction

class DocumentCollection[D <: Document[D]](override val graph: Graph,
                                           override val model: DocumentModel[D],
                                           override val `type`: CollectionType,
                                           override val indexes: List[Index],
                                           override val transaction: Option[Transaction],
                                           override val options: CollectionOptions) extends QueryWritableCollection[D]