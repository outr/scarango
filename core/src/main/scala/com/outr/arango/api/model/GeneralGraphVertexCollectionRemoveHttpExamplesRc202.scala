package com.outr.arango.api.model

import io.circe.Json

/**
  * GeneralGraphVertexCollectionRemoveHttpExamplesRc202
  *
  * @param error Flag if there was an error (true) or not (false).
  *        It is false in this response.
  * @param code The response code.
  * @param graph *** No description ***
  *
  * WARNING: This code is generated by youi-plugin's generateHttpClient. Do not modify directly.
  */
case class GeneralGraphVertexCollectionRemoveHttpExamplesRc202(error: Boolean,
                                                               code: Option[Int] = None,
                                                               graph: Option[GraphRepresentation] = None)