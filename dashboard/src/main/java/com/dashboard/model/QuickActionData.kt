package com.dashboard.model

import com.dashboard.R
import com.dashboard.constant.RecyclerViewItemType
import com.dashboard.recyclerView.AppBaseRecyclerViewItem
import com.framework.base.BaseResponse

data class QuickActionData(
    var items: ArrayList<QuickActionItem>? = null,
) : BaseResponse(), AppBaseRecyclerViewItem {

  var recyclerViewItemType: Int = RecyclerViewItemType.QUICK_ACTION_ITEM_VIEW.getLayout()

  override fun getViewType(): Int {
    return recyclerViewItemType
  }

  enum class QuickActionType(var icon: Int) {
    POST_STATUS_STORY(R.drawable.ic_post_new_story_d), POST_NEW_UPDATE(R.drawable.ic_post_edit_d),
    PLACE_ORDER_APT_BOOKING(R.drawable.ic_new_apt_d), ADD_PHOTO_GALLERY(R.drawable.ic_add_photo_d),
    ADD_TESTIMONIAL(R.drawable.ic_add_testmonial_d), ADD_CUSTOM_PAGE(R.drawable.ic_custom_page_d);

    companion object {
      fun from(name: String): QuickActionType? = values().firstOrNull { it.name == name }
    }
  }
}

data class QuickActionDataResponse(
    var data: ArrayList<QuickActionData>? = null,
) : BaseResponse()