package com.dashboard.model

import com.dashboard.R
import com.dashboard.constant.RecyclerViewItemType
import com.dashboard.recyclerView.AppBaseRecyclerViewItem
import com.framework.base.BaseResponse
import com.framework.utils.getNumberFormat
import com.inventoryorder.model.summary.SummaryEntity

class GrowthStatsData(
    var title: String? = null,
    var dayAmountTitle: String? = null,
    var value: String? = null,
    var icon1: Int? = null,
    var isRupeeSymbols: Boolean = false,
) : BaseResponse(), AppBaseRecyclerViewItem {

  var recyclerViewItemType: Int = RecyclerViewItemType.GROWTH_STATE_ITEM_VIEW.getLayout()

  override fun getViewType(): Int {
    return recyclerViewItemType
  }

  fun getData(summary: SummaryEntity?): ArrayList<GrowthStatsData> {
    val total = summary?.noOfUniqueViews ?: 0 + (summary?.noOfSubscribers ?: 0)
    val list = ArrayList<GrowthStatsData>()
    list.add(GrowthStatsData(title = "All Visits", value = getNumberFormat(total.toString()), icon1 = R.drawable.ic_all_visit_d))
    list.add(GrowthStatsData(title = "Unique\nvisits", value = summary?.getNoOfUniqueViews(), icon1 = R.drawable.ic_uniqe_visit_d))
    list.add(GrowthStatsData(title = "Address\nviews", value = "0", icon1 = R.drawable.ic_address_view_d))
    list.add(GrowthStatsData(title = "Newsletter\nSubscribers", value = summary?.getNoOfSubscribers(), icon1 = R.drawable.ic_news_subcription_d))
    list.add(GrowthStatsData(title = "Search\nQueries", value = "0", icon1 = R.drawable.ic_search_queries_d))
    return list
  }
}