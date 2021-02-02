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
) : BaseResponse(), AppBaseRecyclerViewItem {

  var recyclerViewItemType: Int = RecyclerViewItemType.BUSINESS_CONTENT_SETUP_ITEM_VIEW.getLayout()

  override fun getViewType(): Int {
    return recyclerViewItemType
  }

  fun getRemainingPercentage(): String {
    return if (percentage ?: 0 >= 100) "$percentage% completed" else  "${100 - (percentage ?: 0)}% remaining"
  }

  fun getPendingText(): String? {
    return drScoreItem?.firstOrNull { !it.isUpdate }?.drScoreUiData?.title
  }

  enum class DrScoreType(var id: String, var title: String, var icon: Int) {
    BASIC_PROFILE("basic_profile", "Basic Profile", R.drawable.ic_add_home_d),
    ADVANCE_PROFILE("advanced_profile", "Advanced Profile", R.drawable.ic_edit_content_d);

    companion object {
      fun fromId(value: String): DrScoreType? = values().firstOrNull { it.id.toLowerCase(Locale.ROOT) == value }
    }
  }
}

fun DrScoreModel.getDrScoreData(drScoreUiList: ArrayList<DrScoreUiData>): ArrayList<DrScoreSetupData> {
  val list = ArrayList<DrScoreSetupData>()
  drs_segment?.forEach { segment ->
    val type = DrScoreSetupData.DrScoreType.fromId(segment.id)
    if (type != null) {
      val drScoreItemList = ArrayList<DrScoreItem>()
      segment.events.entries.forEach { event ->
        val drScoreUi = drScoreUiList.firstOrNull { it.id.equals(event.key, ignoreCase = true) }
        if (drScoreUi != null) drScoreItemList.add(DrScoreItem(drScoreUi, (event.value.state == 1)))
      }
      list.add(DrScoreSetupData(type, segment.getScore(), drScoreItemList))
    }
  }
  return list
}


