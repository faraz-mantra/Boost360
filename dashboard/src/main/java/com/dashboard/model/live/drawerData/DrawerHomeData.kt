package com.dashboard.model.live.drawerData

import com.dashboard.R
import com.dashboard.constant.RecyclerViewItemType
import com.dashboard.recyclerView.AppBaseRecyclerViewItem
import com.framework.base.BaseResponse

data class DrawerHomeDataResponse(
    val data: ArrayList<DrawerHomeData>? = null,
) : BaseResponse()

class DrawerHomeData(
    var title: String? = null,
    var newBtnText: String? = null,
    var isNewBtnShow: Boolean = false,
    var isLockShow: Boolean = false,
    var isUpLineShow: Boolean = false,
    var isBottomLineShow: Boolean = false,
    var navType: String = "",
) : BaseResponse(), AppBaseRecyclerViewItem {

  override fun getViewType(): Int {
    return RecyclerViewItemType.HOME_DRAWER_VIEW.getLayout()
  }

  enum class NavType(var icon: Int) {
    NAV_HOME(R.drawable.ic_nav_dashboard_d), NAV_DIGITAL_CHANNEL(R.drawable.ic_nav_content_sharing_d), NAV_MANAGE_CONTENT(R.drawable.ic_nav_content_d),
    NAV_CALLS(R.drawable.ic_nav_calls_d), NAV_ENQUIRY(R.drawable.ic_nav_enquiries_d), NAV_ORDER_APT_BOOKING(R.drawable.ic_nav_orders_d),
    NAV_NEWS_LETTER_SUB(R.drawable.ic_nav_inbox_d), NAV_ADD_ONS_MARKET(R.drawable.ic_nav_addons_d), NAV_BOOST_KEYBOARD(R.drawable.ic_nav_keyboard_d),
    NAV_SETTING(R.drawable.ic_nav_settings_d), NAV_HELP_SUPPORT(R.drawable.ic_nav_help_support_d), NAV_ABOUT_BOOST(R.drawable.ic_nav_about_d),
    NAV_REFER_FRIEND(R.drawable.ic_nav_share_site_d);

    companion object {
      fun from(name: String): NavType? = values().firstOrNull { it.name == name }
    }
  }
}