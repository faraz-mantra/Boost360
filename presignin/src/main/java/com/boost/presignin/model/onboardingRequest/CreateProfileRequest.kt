package com.boost.presignin.model.onboardingRequest

import com.boost.presignin.model.BusinessInfoModel
import com.boost.presignin.model.category.CategoryDataModel
import com.framework.base.BaseRequest
import com.framework.base.BaseResponse
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class CreateProfileRequest(
  @SerializedName("AuthToken")
  var AuthToken: String? = null,
  @SerializedName("ClientId")
  var ClientId: String? = null,
  @SerializedName("LoginKey")
  var LoginKey: String? = null,
  @SerializedName("LoginSecret")
  var LoginSecret: String? = null,
  @SerializedName("ProfileProperties")
  var ProfileProperties: BusinessInfoModel,
  @SerializedName("Provider")
  var Provider: String? = null,
  var profileId: String? = null,
) : BaseRequest(), Serializable
