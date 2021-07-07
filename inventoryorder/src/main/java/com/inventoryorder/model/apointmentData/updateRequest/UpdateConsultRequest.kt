package com.inventoryorder.model.apointmentData.updateRequest

import com.framework.base.BaseRequest
import com.framework.utils.convertObjToString
import com.google.gson.annotations.SerializedName
import com.inventoryorder.model.apointmentData.addRequest.CustomerInfo
import java.io.Serializable

data class UpdateConsultRequest(
  @SerializedName("Query")
  var query: String? = null,
  @SerializedName("UpdateValue")
  var updateValue: String? = null
) : BaseRequest(), Serializable {

  fun setQueryData(id: String?) {
    this.query = String.format("{_id: '%s' }", id)
  }

  fun setUpdateValueAll(updateSet: UpdateConsultField) {
    this.updateValue = convertObjToString(updateSet)
  }
}