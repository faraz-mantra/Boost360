package com.boost.marketplace.infra.api.models.live.addOns

import com.boost.marketplace.R
import com.boost.marketplace.constant.RecyclerViewItemType
import com.boost.marketplace.recyclerView.AppBaseRecyclerViewItem
import com.framework.base.BaseResponse
import com.framework.utils.*
import com.google.gson.annotations.SerializedName
import java.io.Serializable

const val LAST_SEEN_DATA = "LAST_SEEN_DATA"

class ManageBusinessData(
  var title: String? = null,
  var businessType: String? = null,
  var premiumCode: String? = null,
  var isLock: Boolean = false,
  var isHide: Boolean = false,
) : BaseResponse(), AppBaseRecyclerViewItem, Serializable {

  override fun getViewType(): Int {
    return RecyclerViewItemType.MANAGE_BUSINESS_ITEM_VIEW.getLayout()
  }


  enum class BusinessType(var type: String, var icon: Int) {
    video_consultations("video_consultations", R.drawable.video_consultations),
    appointment_settings("appointment_settings", R.drawable.appointment_settings),
    picture_gallery("picture_gallery", R.drawable.picture_gallery);

    //R.drawable.ic_project_terms_d

    companion object {
      fun fromName(name: String?): BusinessType? = values().firstOrNull { it.name == name }
    }
  }

  fun getLastSeenData(): ArrayList<ManageBusinessData> {
    return ArrayList(convertStringToList(PreferencesUtils.instance.getData(LAST_SEEN_DATA, "") ?: "") ?: ArrayList()
    )
  }

  fun saveLastSeenData(data: ManageBusinessData) {
    val addOnsLast = getLastSeenData()
    val matchData = addOnsLast.firstOrNull { it.businessType == data.businessType }
    matchData?.let { addOnsLast.remove(matchData) }
    addOnsLast.add(0, data)
    if (addOnsLast.size > 8) addOnsLast.removeLast()
    PreferencesUtils.instance.saveData(LAST_SEEN_DATA, convertListObjToString(addOnsLast) ?: "")
  }

}

data class ManageBusinessDataResponse(
  var data: ArrayList<ManageActionData>? = null,
) : BaseResponse(), Serializable

data class ManageActionData(
  @SerializedName("action_item")
  var actionItem: ArrayList<ManageBusinessData>? = null,
  @SerializedName("type")
  var type: String? = null,
) : Serializable
