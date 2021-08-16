package com.appservice.model.serviceProduct.update

import java.io.Serializable

data class UpdateValue(
  var key: String? = null,
  var value: String? = null
) : Serializable