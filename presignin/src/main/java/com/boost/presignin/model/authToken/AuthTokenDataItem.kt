package com.boost.presignin.model.authToken

import com.boost.presignin.constant.RecyclerViewItemType
import com.boost.presignin.recyclerView.AppBaseRecyclerViewItem
import com.framework.pref.UserSessionManager
import com.framework.utils.convertObjToString
import com.framework.utils.convertStringToObj
import com.google.gson.annotations.SerializedName
import java.io.Serializable

const val AUTH_TOKEN_SAVE = "AUTH_TOKEN_SAVE"

data class AuthTokenDataItem(
    @SerializedName("AuthenticationToken")
    var authenticationToken: String? = null,
    @SerializedName("Description")
    var description: String? = null,
    @SerializedName("FloatingPointId")
    var floatingPointId: String? = null,
    @SerializedName("FloatingPointTag")
    var floatingPointTag: String? = null,
    @SerializedName("LogoUrl")
    var logoUrl: String? = null,
    @SerializedName("Name")
    var name: String? = null,
    @SerializedName("RootAliasUri")
    var rootAliasUri: String? = null,
    var isItemSelected: Boolean? = false,
) : Serializable, AppBaseRecyclerViewItem {

  override fun getViewType(): Int {
    return RecyclerViewItemType.BUSINESS_LIST_ITEM.getLayout()
  }
}

fun UserSessionManager.getAuthTokenData(): AuthTokenDataItem? {
  return convertStringToObj(pref.getString(AUTH_TOKEN_SAVE, "") ?: "")
}

fun UserSessionManager.saveAuthTokenData(auth: AuthTokenDataItem) {
  editor.putString(AUTH_TOKEN_SAVE, convertObjToString(auth) ?: "")
  editor.apply()
}