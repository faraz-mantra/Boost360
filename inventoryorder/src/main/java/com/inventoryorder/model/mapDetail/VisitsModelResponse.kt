package com.inventoryorder.model.mapDetail

import com.framework.base.BaseResponse
import com.framework.utils.PreferencesUtils
import com.framework.utils.getData
import com.framework.utils.saveData
import com.google.gson.annotations.SerializedName
import java.io.Serializable

const val TOTAL_MAP_VISIT = "TOTAL_MAP_VISIT"

data class VisitsModelResponse(
  @SerializedName("batchType")
  var batchType: String? = null,
  @SerializedName("uniqueVisitsList")
  var uniqueVisitsList: ArrayList<UniqueVisitsList>? = null,
) : BaseResponse(), Serializable {

  enum class BatchType(var value: Int) {
    dy(0), ww(1), mm(2), yy(3);
  }

  fun getTotalCount(): String {
    var count = 0
    uniqueVisitsList?.forEach { count += it.dataCount ?: 0 }
    return count.toString()
  }

  fun getTotalOMapVisit(key: String): String? {
    return PreferencesUtils.instance.getData(key, "0")
  }

  fun saveMapVisit(key: String) {
    PreferencesUtils.instance.saveData(key, getTotalCount())
  }
}

data class UniqueVisitsList(
  @SerializedName("DataCount")
  var dataCount: Int? = null,
  @SerializedName("batchNumber")
  var batchNumber: Int? = null,
  @SerializedName("endDate")
  var endDate: String? = null,
  @SerializedName("startDate")
  var startDate: String? = null,
) : Serializable

