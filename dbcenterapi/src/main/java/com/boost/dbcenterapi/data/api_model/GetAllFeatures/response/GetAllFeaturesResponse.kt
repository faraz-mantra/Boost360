package com.boost.dbcenterapi.data.api_model.GetAllFeatures.response

import com.framework.base.BaseResponse
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class GetAllFeaturesResponse(
  @SerializedName("Data")
  val `Data`: List<Data>,
  @SerializedName("Extra")
  val `Extra`: Extra
) : BaseResponse(), Serializable