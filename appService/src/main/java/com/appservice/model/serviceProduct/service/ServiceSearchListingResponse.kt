package com.appservice.model.serviceProduct.service

import com.framework.base.BaseResponse
import com.google.gson.annotations.SerializedName

data class ServiceSearchListingResponse(
  @field:SerializedName("StatusCode")
  val statusCode: Int? = null,
  @field:SerializedName("Result")
  val result: Result? = null,
) : BaseResponse()

data class Result(
  @field:SerializedName("Paging")
  val paging: Paging? = null,
  @field:SerializedName("Data")
  val data: ArrayList<ItemsItem?>? = null,
)

data class Paging(
  @field:SerializedName("Skip")
  val skip: Int? = null,
  @field:SerializedName("Limit")
  val limit: Int? = null,
  @field:SerializedName("Count")
  val count: Int? = null,
)

