package com.appservice.model.serviceProduct.addProductImage


import com.framework.base.BaseRequest
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ProductImageRequest(
  @SerializedName("ActionData")
  var actionData: ActionDataI? = null,
  @SerializedName("WebsiteId")
  var websiteId: String? = null
) : BaseRequest(), Serializable