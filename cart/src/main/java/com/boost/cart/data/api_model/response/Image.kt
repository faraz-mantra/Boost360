package com.boost.cart.data.api_model.GetAllFeatures.response

import java.io.Serializable

data class Image(
  val _kid: String,
  val _parentClassId: String,
  val _parentClassName: String,
  val _propertyName: String,
  val createdon: String,
  val description: String,
  val isarchived: Boolean,
  val updatedon: String,
  val url: String,
  val websiteid: String
) : Serializable