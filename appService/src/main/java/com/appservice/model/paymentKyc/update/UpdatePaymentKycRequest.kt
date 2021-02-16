package com.appservice.model.paymentKyc.update


import com.framework.base.BaseRequest
import com.framework.utils.convertObjToString
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class UpdatePaymentKycRequest(
    @SerializedName("Query")
    var query: String? = null,
    @SerializedName("UpdateValue")
    var updateValue: String? = null
) : BaseRequest(),Serializable {

  fun setUpdateValueKyc(updateKycValue: UpdateKycValue) {
    this.updateValue = convertObjToString(updateKycValue)
  }
}