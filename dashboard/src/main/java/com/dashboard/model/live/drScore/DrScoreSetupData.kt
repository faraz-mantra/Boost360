package com.dashboard.model.live.drScore

import com.dashboard.R
import com.dashboard.constant.RecyclerViewItemType
import com.dashboard.recyclerView.AppBaseRecyclerViewItem
import com.framework.base.BaseResponse
import com.framework.models.firestore.DrScoreModel
import java.util.*
import kotlin.collections.ArrayList

data class DrScoreSetupData(
  var type: DrScoreType? = null,
  var percentage: Int? = null,
  var drScoreItem: ArrayList<DrScoreItem>? = null,
) : BaseResponse(), AppBaseRecyclerViewItem, Comparable<DrScoreSetupData> {

  var recyclerViewItemType: Int = RecyclerViewItemType.BUSINESS_CONTENT_SETUP_ITEM_VIEW.getLayout()

  override fun getViewType(): Int {
    return recyclerViewItemType
  }

  fun getRemainingPercentage(): String {
    return if (percentage ?: 0 >= 100) "$percentage% completed" else if (percentage!! == 0) "get started" else "${100 - (percentage ?: 0)}% remaining"
  }

  fun getPendingText(): String? {
    return drScoreItem?.firstOrNull { !it.isUpdate }?.drScoreUiData?.title
  }

  fun getDrScoreData(): DrScoreUiData? {
    return drScoreItem?.firstOrNull { !it.isUpdate }?.drScoreUiData
  }

  enum class DrScoreType(var id: String, var title: String, var icon: Int) {
    BUSINESS_PROFILE("business_profile", "Business profile", R.drawable.ic_business_profile_dr),
    WEBSITE_CONTENT("website_content", "Website content", R.drawable.ic_edit_content_d),
    ECOMMERCE_SETTINGS(
      "enable_ecommerce",
      "E-commerce settings",
      R.drawable.ic_e_commerce_setting_dr
    ),
    CUSTOMER_INTERACTIONS(
      "customer_interactions",
      "Customer interactions",
      R.drawable.ic_customer_interaction_dr
    ),
    MARKETING_ENGAGEMENT(
      "marketing_enagement",
      "Marketing & Engagement",
      R.drawable.ic_marketing_engage_dr
    );
    //Testing Data
//    BASIC_PROFILE("basic_profile", "Basic Profile", R.drawable.ic_add_home_d),
//    ADVANCE_PROFILE("advanced_profile", "Advanced Profile", R.drawable.ic_edit_content_d);

    companion object {
      fun fromId(value: String): DrScoreType? =
        values().firstOrNull { it.id.toLowerCase(Locale.ROOT) == value }
    }
  }

  override fun compareTo(other: DrScoreSetupData): Int {
    return when (other.percentage) {
      100 -> -1
      else -> 1
    }
  }
}

fun DrScoreModel.getDrScoreData(drScoreUiList: ArrayList<DrScoreUiData>): ArrayList<DrScoreSetupData> {
  val list = ArrayList<DrScoreSetupData>()
  drs_segment?.forEach { segment ->
    val type = DrScoreSetupData.DrScoreType.fromId(segment.id)
    if (type != null) {
      var drScoreItemList = ArrayList<DrScoreItem>()
      segment.events.entries.forEach { event ->
        val drScoreUi = drScoreUiList.firstOrNull { it.id.equals(event.key, ignoreCase = true) }
        if (drScoreUi != null) drScoreItemList.add(DrScoreItem(drScoreUi, (event.value.state == 1)))
      }
      if(type.name == DrScoreSetupData.DrScoreType.BUSINESS_PROFILE.name){
        drScoreItemList= ArrayList(drScoreItemList.sortedBy { DrScoreItem.DrScoreItemType.fromName(it.drScoreUiData?.id)?.priority?:0 })
      }
      list.add(DrScoreSetupData(type, segment.getScore(), drScoreItemList))
    }
  }
  return ArrayList(list.sorted())
}


