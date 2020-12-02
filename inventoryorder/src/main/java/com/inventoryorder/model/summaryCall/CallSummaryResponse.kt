package com.inventoryorder.model.summaryCall

import com.framework.base.BaseResponse
import com.framework.utils.PreferencesUtils
import com.framework.utils.convertObjToString
import com.framework.utils.convertStringToObj
import com.framework.utils.getData
import com.google.gson.annotations.SerializedName
import java.io.Serializable

const val CALL_SUMMARY_DATA = "CALL_SUMMARY_DATA"

data class CallSummaryResponse(
    @SerializedName("MissedCalls")
    var missedCalls: Int? = null,
    @SerializedName("ReceivedCalls")
    var receivedCalls: Int? = null,
    @SerializedName("TotalCalls")
    var totalCalls: Int? = null,
) : BaseResponse(), Serializable {

  fun getCallSummary(): CallSummaryResponse? {
    val resp = PreferencesUtils.instance.getData(CALL_SUMMARY_DATA, "") ?: ""
    return convertStringToObj(resp)
  }

  fun saveData() {
    PreferencesUtils.instance.saveDataN(CALL_SUMMARY_DATA, convertObjToString(this) ?: "")
  }
}