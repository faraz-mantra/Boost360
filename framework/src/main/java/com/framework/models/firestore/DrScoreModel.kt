package com.framework.models.firestore;
import com.google.gson.annotations.SerializedName

class DrScoreModel (

		@SerializedName("fp_tag")
		var fp_tag : String? = null,
		@SerializedName("fp_id")
		var fp_id : String? = null,
		@SerializedName("client_id")
		var client_id : String? = null,
		@SerializedName("drs_total")
		var drs_total : Double? = null,
		@SerializedName("drs_segment")
		var drs_segment : List<Drs_segment>? = null,
		@SerializedName("metricdetail")
		var metricdetail : Metricdetail? = null
)