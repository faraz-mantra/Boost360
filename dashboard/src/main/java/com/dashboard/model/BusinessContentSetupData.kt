package com.dashboard.model

import com.dashboard.R
import com.dashboard.constant.RecyclerViewItemType
import com.dashboard.recyclerView.AppBaseRecyclerViewItem
import com.framework.base.BaseResponse
import java.io.Serializable

class BusinessContentSetupData(
    var title: String? = null,
    var subTitle: String? = null,
    var icon1: Int? = null,
    var icon2: Int? = null,
    var gifIcon: Int? = null,
    var businessData: ArrayList<AllBusinessData>? = null,
    var type: String? = null
) : BaseResponse(), Serializable, AppBaseRecyclerViewItem {

  var recyclerViewItemType: Int = RecyclerViewItemType.BUSINESS_CONTENT_SETUP_ITEM_VIEW.getLayout()
  override fun getViewType(): Int {
    return recyclerViewItemType
  }

  fun getData(): ArrayList<BusinessContentSetupData> {
    val list = ArrayList<BusinessContentSetupData>()
    list.add(BusinessContentSetupData(title = "Business profile setup", subTitle = "40% remaining", icon1 = R.drawable.ic_add_home_circle_d, icon2 = R.drawable.ic_add_home_d, businessData = getListBusiness(), type = ActiveViewType.PROFILE_SETUP.name))
    list.add(BusinessContentSetupData(title = "Content management", subTitle = "68% remaining", icon1 = R.drawable.ic_edit_content_circle_d, icon2 = R.drawable.ic_edit_content_d, businessData = getListContent(), type = ActiveViewType.MANAGEMENT.name))
    list.add(BusinessContentSetupData(title = "Online channels sync", subTitle = "100% completed", gifIcon = R.raw.ic_ok_gif_d, businessData = getListChannelSync(), type = ActiveViewType.ONLINE_SYNC.name))
    return list
  }

  private fun getListBusiness(): ArrayList<AllBusinessData> {
    val list = ArrayList<AllBusinessData>()
    list.add(AllBusinessData(title = "Business Name", subTitle = "Add name of your business.", isDone = true, icon1 = R.drawable.ic_ok_11_d))
    list.add(AllBusinessData(title = "Business Logo", subTitle = "Add a visual identity of your Brand.", isDone = true, icon1 = R.drawable.ic_ok_11_d))
    list.add(AllBusinessData(title = "Business Description", subTitle = "Describe your business in around 200 characters.", icon1 = R.drawable.ic_circle_grey_9_d))
    list.add(AllBusinessData(title = "Location on map", subTitle = "This helps your customers navigate to your business.", icon1 = R.drawable.ic_circle_grey_9_d))
    list.add(AllBusinessData(title = "Featured Image/Video", subTitle = "This shows up along with your business description in the about section.", icon1 = R.drawable.ic_circle_grey_9_d, isLast = true))
    return list
  }

  private fun getListContent(): ArrayList<AllBusinessData> {
    val list = ArrayList<AllBusinessData>()
    list.add(AllBusinessData(title = "Business Name", subTitle = "Add name of your business.", isDone = true, icon1 = R.drawable.ic_ok_11_d))
    list.add(AllBusinessData(title = "Business Logo", subTitle = "Add a visual identity of your Brand.", isDone = true, icon1 = R.drawable.ic_ok_11_d))
    list.add(AllBusinessData(title = "Business Description", subTitle = "Describe your business in around 200 characters.", icon1 = R.drawable.ic_circle_grey_9_d))
    list.add(AllBusinessData(title = "Location on map", subTitle = "Effects of pH, temperature.", icon1 = R.drawable.ic_circle_grey_9_d, isLast = true))
    return list
  }

  private fun getListChannelSync(): ArrayList<AllBusinessData> {
    val list = ArrayList<AllBusinessData>()
    list.add(AllBusinessData(title = "Business Name", subTitle = "Add name of your business.", isDone = true, icon1 = R.drawable.ic_ok_11_d))
    list.add(AllBusinessData(title = "Business Logo", subTitle = "Add a visual identity of your Brand.", isDone = true, icon1 = R.drawable.ic_ok_11_d))
    list.add(AllBusinessData(title = "Business Description", subTitle = "Describe your business in around 200 characters.", isDone = true, icon1 = R.drawable.ic_ok_11_d))
    list.add(AllBusinessData(title = "Location on map", subTitle = "Effects of pH, temperature.", isDone = true, icon1 = R.drawable.ic_ok_11_d, isLast = true))
    return list
  }

  enum class ActiveViewType {
    PROFILE_SETUP, MANAGEMENT, ONLINE_SYNC
  }
}

data class AllBusinessData(
    var title: String? = null,
    var subTitle: String? = null,
    var isDone: Boolean? = false,
    var icon1: Int? = null,
    var isLast: Boolean? = false,
) : Serializable, AppBaseRecyclerViewItem {

  var recyclerViewItemType: Int = RecyclerViewItemType.ITEMS_CONTENT_SETUP_ITEM_VIEW.getLayout()

  override fun getViewType(): Int {
    return recyclerViewItemType
  }
}