package com.inventoryorder.model.mapDetail

import com.framework.base.BaseResponse
import com.google.gson.annotations.SerializedName
import java.io.Serializable

const val VISIT_SUMMARY_DATA = "VISIT_SUMMARY_DATA"

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

