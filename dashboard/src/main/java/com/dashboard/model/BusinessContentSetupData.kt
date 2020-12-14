package com.dashboard.model

import com.dashboard.R
import com.dashboard.constant.RecyclerViewItemType
import com.dashboard.model.live.SiteMeterModel
import com.dashboard.model.live.siteMeter.SiteMeterScoreDetails
import com.dashboard.model.live.siteMeter.getCompletePercentage
import com.dashboard.model.live.siteMeter.getRemainingPercentage
import com.dashboard.recyclerView.AppBaseRecyclerViewItem
import com.framework.base.BaseResponse
import java.io.Serializable

class BusinessContentSetupData(
    var title: String? = null,
    var subTitle: String? = null,
    var percentage: Int? = null,
    var icon2: Int? = null,
    var businessData: ArrayList<SiteMeterModel>? = null,
    var gifIcon: Int? = null,
    var type: String? = null,
) : BaseResponse(), Serializable, AppBaseRecyclerViewItem {

  var recyclerViewItemType: Int = RecyclerViewItemType.BUSINESS_CONTENT_SETUP_ITEM_VIEW.getLayout()

  override fun getViewType(): Int {
    return recyclerViewItemType
  }

  enum class ActiveViewType {
    PROFILE_SETUP, MANAGEMENT, ONLINE_SYNC
  }

  fun getPendingText(): String? {
    return businessData?.firstOrNull { it.status == false }?.Title
  }

  fun getCompleteText(): String? {
    return if (type == ActiveViewType.PROFILE_SETUP.name) "Business Profile Sync" else "Content Management Sync"
  }
}

fun SiteMeterScoreDetails.getListDigitalScore(): ArrayList<BusinessContentSetupData> {
  val listDigitalScore = ArrayList<BusinessContentSetupData>()
  listDigitalScore.add(BusinessContentSetupData(title = "Business profile setup", subTitle = "${businessProfile.getRemainingPercentage()}% remaining", percentage = businessProfile.getRemainingPercentage(), icon2 = R.drawable.ic_add_home_d, businessData = businessProfile, gifIcon = R.raw.ic_next_arrow_gif_d, type = BusinessContentSetupData.ActiveViewType.PROFILE_SETUP.name))
  listDigitalScore.add(BusinessContentSetupData(title = "Content management", subTitle = "${contentManagement.getRemainingPercentage()}% remaining", percentage = contentManagement.getRemainingPercentage(), icon2 = R.drawable.ic_edit_content_d, businessData = contentManagement, gifIcon = R.raw.ic_next_arrow_gif_d, type = BusinessContentSetupData.ActiveViewType.MANAGEMENT.name))
  listDigitalScore.add(BusinessContentSetupData(title = "Online channels sync", subTitle = "${channelSync.getCompletePercentage()}% completed", businessData = channelSync, gifIcon = R.raw.ic_ok_gif_d, type = BusinessContentSetupData.ActiveViewType.ONLINE_SYNC.name))
  return listDigitalScore
}
