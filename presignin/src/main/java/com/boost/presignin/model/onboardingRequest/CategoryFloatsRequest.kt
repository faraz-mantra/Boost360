package com.boost.presignin.model.onboardingRequest

import com.boost.presignin.model.category.CategoryDataModel
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class CategoryFloatsRequest(
    val requestProfile: CreateProfileRequest? = null,
    var categoryDataModel: CategoryDataModel? = null,
    var businessUrl: String? = null,
    var webSiteUrl: String? = null,
    var whatsAppFlag: Boolean? = null,
    var floatingPointId: String? = null,
    var fpTag: String? = null,
    var isUpdate: Boolean? = false,
    @SerializedName("businessName")
    var businessName: String? = null,
    @SerializedName("domainName")
    var domainName: String? = null,
    @SerializedName("userBusinessEmail")
    var userBusinessEmail: String? = "",
    @SerializedName("userBusinessMobile")
    var userBusinessMobile: String? = null,
) : Serializable {

  fun getWebSiteId(): String? {
    return if (isUpdate == true) fpTag else domainName
  }

}