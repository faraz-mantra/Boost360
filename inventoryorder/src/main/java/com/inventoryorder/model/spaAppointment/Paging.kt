package com.inventoryorder.model.spaAppointment

import java.io.Serializable

data class Paging(
  var Count: Int? = null,
  var Limit: Int? = null,
  var Skip: Int? = null
) : Serializable