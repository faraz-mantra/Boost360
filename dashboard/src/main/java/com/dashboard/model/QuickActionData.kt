package com.dashboard.model

import com.dashboard.R
import com.dashboard.constant.RecyclerViewItemType
import com.dashboard.recyclerView.AppBaseRecyclerViewItem
import com.framework.base.BaseResponse

class QuickActionData(
    var items: ArrayList<QuickActionItem>? = null,
) : BaseResponse(), AppBaseRecyclerViewItem {
  var recyclerViewItemType: Int = RecyclerViewItemType.QUICK_ACTION_ITEM_VIEW.getLayout()
  override fun getViewType(): Int {
    return recyclerViewItemType
  }

  fun getData(): ArrayList<QuickActionData> {
    val list = ArrayList<QuickActionData>()
    val items1 = ArrayList<QuickActionItem>()
    items1.add(QuickActionItem(title = "Post New\nStatus/Story", icon1 = R.drawable.ic_post_new_story_d))
    items1.add(QuickActionItem(title = "Post new \nUpdate", icon1 = R.drawable.ic_post_edit_d))
    items1.add(QuickActionItem(title = "Place New\nOrder", icon1 = R.drawable.ic_new_apt_d))
    items1.add(QuickActionItem(title = "Add photo\nto gallery", icon1 = R.drawable.ic_add_photo_d))
    list.add(QuickActionData(items1))
    val items2 = ArrayList<QuickActionItem>()
    items2.add(QuickActionItem(title = "Add new\ntestimonial", icon1 = R.drawable.ic_add_testmonial_d))
    items2.add(QuickActionItem(title = "Add new\ncustom page", icon1 = R.drawable.ic_custom_page_d))
    list.add(QuickActionData(items2))
    return list
  }

}