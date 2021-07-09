package com.inventoryorder.model.summaryCall

import com.framework.base.BaseResponse
import com.framework.utils.PreferencesUtils
import com.framework.utils.getData
import com.framework.utils.getNumberFormat
import com.framework.utils.saveData
import com.google.gson.annotations.SerializedName
import java.io.Serializable

const val CALL_BUSINESS_REPORT = "CALL_BUSINESS_REPORT"
const val CALL_MY_ENQUIRIES = "CALL_MY_ENQUIRIES"

data class CallSummaryResponse(
    @SerializedName("MissedCalls")
    var missedCalls: Int? = null,
    @SerializedName("ReceivedCalls")
    var receivedCalls: Int? = null,
    @SerializedName("TotalCalls")
    var totalCalls: Int? = null,
) : BaseResponse(), Serializable {

  fun getTotalCalls(): String {
    return getNumberFormat((totalCalls ?: 0).toString())
  }

  fun getCallSummary(key: String): String? {
    return PreferencesUtils.instance.getData(key, "")
  }

  fun saveData(key: String) {
    PreferencesUtils.instance.saveData(key, getTotalCalls())
  }
}