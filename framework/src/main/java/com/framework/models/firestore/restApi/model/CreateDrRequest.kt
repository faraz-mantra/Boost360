package com.framework.models.firestore.restApi.model


import com.google.gson.annotations.SerializedName

data class CreateDrRequest(
  @SerializedName("fpTag")
  var fpTag: String? = null
)