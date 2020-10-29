package com.dashboard.model

import com.dashboard.R
import com.dashboard.constant.RecyclerViewItemType
import com.dashboard.recyclerView.AppBaseRecyclerViewItem
import com.framework.base.BaseResponse

class GrowthStatsData(
    var title: String? = null,
    var dayAmountTitle: String? = null,
    var amount: String? = null,
    var icon1: Int? = null,
    var isRupeeSymbols: Boolean = false
) : BaseResponse(), AppBaseRecyclerViewItem {

  var recyclerViewItemType: Int = RecyclerViewItemType.GROWTH_STATE_ITEM_VIEW.getLayout()
  override fun getViewType(): Int {
    return recyclerViewItemType
  }

  fun getData(): ArrayList<GrowthStatsData> {
    val list = ArrayList<GrowthStatsData>()
    list.add(GrowthStatsData(title = "All Visits", dayAmountTitle = "41 today", amount = "3,034", icon1 = R.drawable.ic_all_visit_d))
    list.add(GrowthStatsData(title = "Unique\nvisits", dayAmountTitle = "5 today", amount = "519", icon1 = R.drawable.ic_uniqe_visit_d))
    list.add(GrowthStatsData(title = "Address\nviews", amount = "753", icon1 = R.drawable.ic_address_view_d))
    list.add(GrowthStatsData(title = "Newsletter\nSubscribers", amount = "50", icon1 = R.drawable.ic_news_subcription_d))
    list.add(GrowthStatsData(title = "Search\nQueries", amount = "24", icon1 = R.drawable.ic_search_queries_d))
    return list
  }
}