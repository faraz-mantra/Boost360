package com.boost.presignin.model.onboardingRequest

import com.boost.presignin.model.category.CategoryDataModel
import com.framework.pref.UserSessionManager
import com.framework.utils.convertObjToString
import com.framework.utils.convertStringToObj
import com.google.gson.annotations.SerializedName
import java.io.Serializable

const val CATEGORY_REQUEST_SAVE = "CATEGORY_REQUEST_SAVE"

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
  var mobilePreview: String? = null,
  var desktopPreview: String? = null,
) : Serializable {

  fun getWebSiteId(): String? {
    return if (isUpdate == true) fpTag else domainName
  }
}

fun UserSessionManager.getCategoryRequest(): CategoryFloatsRequest? {
  return convertStringToObj(pref.getString(CATEGORY_REQUEST_SAVE, "") ?: "")
}

fun UserSessionManager.saveCategoryRequest(request: CategoryFloatsRequest) {
  editor.putString(CATEGORY_REQUEST_SAVE, convertObjToString(request) ?: "")
  editor.apply()
}