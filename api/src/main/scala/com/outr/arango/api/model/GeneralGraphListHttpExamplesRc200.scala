package com.outr.arango.api.model

import io.circe.Json


case class GeneralGraphListHttpExamplesRc200(error: Boolean,
                                             code: Option[Int] = None,
                                             graphs: Option[GraphList] = None)