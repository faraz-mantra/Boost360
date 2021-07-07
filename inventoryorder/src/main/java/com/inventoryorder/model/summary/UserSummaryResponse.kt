package com.inventoryorder.model.summary


import com.framework.base.BaseResponse
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class UserSummaryResponse(
  @SerializedName("Entity")
  var entity: ArrayList<SummaryEntity>? = null,
  @SerializedName("ErrorList")
  var errorList: List<Any>? = null,
  @SerializedName("OperationStatus")
  var operationStatus: Boolean? = null,
  @SerializedName("ReferenceId")
  var referenceId: Any? = null,
) : BaseResponse(), Serializable {

  fun getSummary(): SummaryEntity? {
    return entity?.firstOrNull()
  }
}