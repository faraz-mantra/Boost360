package com.appservice.model.serviceProduct.gstProduct.update

import com.framework.base.BaseRequest
import com.framework.utils.convertObjToString
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ProductUpdateRequest(
    @SerializedName("Multi")
    var multi: Boolean? = null,
    @SerializedName("Query")
    var query: String? = null,
    @SerializedName("UpdateValue")
    var updateValue: String? = null
) : BaseRequest(), Serializable {

  fun updateValueSet(updateValue: UpdateValueU) {
    this.updateValue = convertObjToString(updateValue)
  }
}