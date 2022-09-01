package com.boost.marketplace.infra.api.models.business.model

import com.google.gson.annotations.SerializedName

data class BusinessProfileUpdateRequest(

  @field:SerializedName("fpTag")
  var fpTag: String? = null,

  @field:SerializedName("clientId")
  var clientId: String? = null,

  @field:SerializedName("updates")
  var updates: List<UpdatesItem?>? = null
)

data class UpdatesItem(

  @field:SerializedName("value")
  var value: String? = null,

  @field:SerializedName("key")
  var key: String? = null
)
