package com.appservice.model.aptsetting

import com.framework.base.BaseResponse
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class DeliveryDetailsResponse(


  @field:SerializedName("StatusCode")
  var statusCode: Int? = null,

  @field:SerializedName("Result")
  var result: DResult? = null,
) : BaseResponse()

data class DResult(

  @field:SerializedName("IsPickupAllowed")
  var isPickupAllowed: Boolean? = null,

  @field:SerializedName("IsBusinessLocationPickupAllowed")
  var isBusinessLocationPickupAllowed: Boolean? = null,

  @field:SerializedName("IsWarehousePickupAllowed")
  var isWarehousePickupAllowed: Boolean? = null,

  @field:SerializedName("IsHomeDeliveryAllowed")
  var isHomeDeliveryAllowed: Boolean? = null,

  @field:SerializedName("FlatDeliveryCharge")
  var flatDeliveryCharge: Double? = null,

  @field:SerializedName("FloatingPointId")
  var floatingPointId: String? = null,
) : Serializable {

  fun getFlatDeliveryCharge(): Double {
    return if (flatDeliveryCharge == null) 0.0 else flatDeliveryCharge!!
  }

}


data class DError(

  @field:SerializedName("ErrorList")
  var errorList: ErrorList? = null,

  @field:SerializedName("ErrorCode")
  var errorCode: Any? = null,
)

data class DErrorList(
  var any: Any? = null,
)
