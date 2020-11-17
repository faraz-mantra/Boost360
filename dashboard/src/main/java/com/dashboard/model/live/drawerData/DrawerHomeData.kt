package com.dashboard.model.live.drawerData

import com.dashboard.constant.RecyclerViewItemType
import com.dashboard.recyclerView.AppBaseRecyclerViewItem
import com.framework.base.BaseResponse

class DrawerHomeData(
    var title: String? = null,
    var icon: Int? = null,
    var newBtnText: String? = null,
    var isNewBtnShow: Boolean = false,
    var isLockShow: Boolean = false,
    var isUpLineShow: Boolean = false,
    var isBottomLineShow: Boolean = false,
) : BaseResponse(), AppBaseRecyclerViewItem {

  var recyclerViewItemType: Int = RecyclerViewItemType.HOME_DRAWER_VIEW.getLayout()
  override fun getViewType(): Int {
    return recyclerViewItemType
  }

  fun getData(): ArrayList<DrawerHomeData> {
    val list = ArrayList<DrawerHomeData>()
    list.add(DrawerHomeData(title = "Home", icon = null))
    list.add(DrawerHomeData(title = "My digital channels", icon = null))
    list.add(DrawerHomeData(title = "Manage Content", icon = null, isUpLineShow = true, isBottomLineShow = true))
    list.add(DrawerHomeData(title = "Calls", icon = null, isLockShow = true))
    list.add(DrawerHomeData(title = "Enquiries", icon = null, isLockShow = true))
    list.add(DrawerHomeData(title = "Orders", icon = null))
    list.add(DrawerHomeData(title = "Newsletter Subscriptions", icon = null))
    list.add(DrawerHomeData(title = "Add-Ons Marketplace", icon = null, newBtnText = "New", isNewBtnShow = true, isUpLineShow = true))
    list.add(DrawerHomeData(title = "Boost Keyboard", icon = null, isLockShow = true))
    list.add(DrawerHomeData(title = "Online Advertising", icon = null, isLockShow = true, isBottomLineShow = true))
    list.add(DrawerHomeData(title = "Help and Support", icon = null, isBottomLineShow = true))
    list.add(DrawerHomeData(title = "About Boost", icon = null))
    list.add(DrawerHomeData(title = "Share My Site", icon = null))
    list.add(DrawerHomeData(title = "Refer a friend", icon = null))
    return list
  }
}