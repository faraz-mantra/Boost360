package com.inventoryorder.model.services

import com.google.gson.annotations.SerializedName

data class ServiceListingRequest(

  @field:SerializedName("Categories")
  val categories: List<Any?>? = null,

  @field:SerializedName("ServiceType")
  val serviceType: String? = null,

  @field:SerializedName("FloatingPointTag")
  val floatingPointTag: String? = null
)
