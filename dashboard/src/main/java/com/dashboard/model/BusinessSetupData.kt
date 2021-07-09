package com.dashboard.model

import com.dashboard.R
import com.dashboard.constant.RecyclerViewItemType
import com.dashboard.recyclerView.AppBaseRecyclerViewItem
import com.framework.base.BaseResponse

class BusinessSetupData(
    var title: String? = null,
    var subTitle: String? = null,
    var btnTitle: String? = null,
    var btnGifIcon: Int? = null,
    var icon1: Int? = null,
    var icon2: Int? = null,
    var gifIcon: Int? = null,
    var type: String? = null
) : BaseResponse(), AppBaseRecyclerViewItem {

  var recyclerViewItemType: Int = RecyclerViewItemType.BUSINESS_SETUP_ITEM_VIEW.getLayout()
  override fun getViewType(): Int {
    return recyclerViewItemType
  }

  fun getData(): ArrayList<BusinessSetupData> {
    val list = ArrayList<BusinessSetupData>()
    list.add(BusinessSetupData(title = "Business profile setup", subTitle = "40% remaining", btnTitle = "Add working hours", btnGifIcon = R.raw.ic_next_arrow_gif_d, icon1 = R.drawable.ic_add_home_circle_d, icon2 = R.drawable.ic_add_home_d, type = ActiveViewType.PROFILE_SETUP.name))
    list.add(BusinessSetupData(title = "Content management", subTitle = "68% remaining", btnTitle = "List your services", btnGifIcon = R.raw.ic_next_arrow_gif_d, icon1 = R.drawable.ic_edit_content_circle_d, icon2 = R.drawable.ic_edit_content_d, type = ActiveViewType.MANAGEMENT.name))
    list.add(BusinessSetupData(title = "Online channels sync", subTitle = "100% completed", btnTitle = "List your services", gifIcon = R.raw.ic_ok_gif_d, type = ActiveViewType.ONLINE_SYNC.name))
    return list
  }

  enum class ActiveViewType {
    PROFILE_SETUP, MANAGEMENT, ONLINE_SYNC
  }
}