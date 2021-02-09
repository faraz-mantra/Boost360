package com.inventoryorder.model.summary

import com.framework.utils.*
import com.google.gson.annotations.SerializedName
import java.io.Serializable

const val TOTAL_USER_MESSAGE = "TOTAL_USER_MESSAGE"
const val USER_BUSINESS_SUMMARY = "USER_BUSINESS_SUMMARY"
const val USER_WEBSITE_REPORT = "USER_WEBSITE_REPORT"
const val USER_MY_ENQUIRIES = "USER_MY_ENQUIRIES"

data class SummaryEntity(
    @SerializedName("NoOfMessages")
    var noOfMessages: Int? = null,
    @SerializedName("NoOfSubscribers")
    var noOfSubscribers: Int? = null,
    @SerializedName("NoOfUniqueViews")
    var noOfUniqueViews: Int? = null,
    @SerializedName("NoOfViews")
    var noOfViews: Int? = null,
) : Serializable {

  fun getNoOfMessages(): String {
    return getNumberFormat((noOfMessages ?: 0).toString())
  }

  fun getNoOfSubscribers(): String {
    return getNumberFormat((noOfSubscribers ?: 0).toString())
  }

  fun getNoOfUniqueViews(): String {
    return getNumberFormat((noOfUniqueViews ?: 0).toString())
  }

  fun getNoOfViews(): String {
    return getNumberFormat((noOfViews ?: 0).toString())
  }

  fun getUserSummary(key: String): SummaryEntity? {
    val resp = PreferencesUtils.instance.getData(key, "") ?: ""
    return convertStringToObj(resp)
  }

  fun saveData(key: String) {
    PreferencesUtils.instance.saveData(key, convertObjToString(this) ?: "")
  }

  fun getTotalUserMessage(key: String): Int {
    return PreferencesUtils.instance.getData(key, 0)
  }

  fun saveTotalMessage(key: String) {
    PreferencesUtils.instance.saveData(key, noOfMessages ?: 0)
  }
}