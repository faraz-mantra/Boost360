package com.framework.models.firestore;

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class DrScoreModel(

    @SerializedName("fp_tag", alternate = ["fpTag"])
    var fp_tag: String? = null,
    @SerializedName("fp_id", alternate = ["fpId"])
    var fp_id: String? = null,
    @SerializedName("client_id", alternate = ["clientId"])
    var client_id: String? = null,
    @SerializedName("drs_total", alternate = ["drsTotal"])
    var drs_total: Double? = null,
    @SerializedName("drs_segment", alternate = ["drsSegment"])
    var drs_segment: List<Drs_segment>? = null,
    @SerializedName("metricdetail", alternate = ["metricDetail"])
    var metricdetail: Metricdetail? = null,
) : Serializable {
  fun getDrsTotal(): Int {
    return (drs_total ?: 0.0).toInt()
  }
}