package com.outr.arango

case class IndexInfo(id: String,
                     index: Index,
                     isNewlyCreated: Option[Boolean] = None,
                     selectivityEstimate: Option[Double] = None)