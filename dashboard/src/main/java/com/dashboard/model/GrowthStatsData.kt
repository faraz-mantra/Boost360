package com.dashboard.model

import com.dashboard.R
import com.dashboard.constant.RecyclerViewItemType
import com.dashboard.pref.UserSessionManager
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
    var type: String?=null,
) : BaseResponse(), AppBaseRecyclerViewItem {

  var recyclerViewItemType: Int = RecyclerViewItemType.GROWTH_STATE_ITEM_VIEW.getLayout()

  override fun getViewType(): Int {
    return recyclerViewItemType
  }

  fun getData(summary: SummaryEntity?, session: UserSessionManager?): ArrayList<GrowthStatsData> {
    val list = ArrayList<GrowthStatsData>()
    list.add(GrowthStatsData(title = "All Visits", value = summary?.getNoOfViews(), icon1 = R.drawable.ic_all_visit_d,type = GrowthType.ALL_VISITS.name))
    list.add(GrowthStatsData(title = "Unique\nvisits", value = summary?.getNoOfUniqueViews(), icon1 = R.drawable.ic_uniqe_visit_d,type = GrowthType.UNIQUE_VISITS.name))
    list.add(GrowthStatsData(title = "Address\nviews", value = getNumberFormat((session?.mapVisitsCount?.toIntOrNull()?:0).toString()), icon1 = R.drawable.ic_address_view_d,type = GrowthType.ADDRESS_NEWS.name))
    list.add(GrowthStatsData(title = "Newsletter\nSubscribers", value = summary?.getNoOfSubscribers(), icon1 = R.drawable.ic_news_subcription_d,type = GrowthType.NEWSLETTER_SUBSCRIPTION.name))
    list.add(GrowthStatsData(title = "Search\nQueries", value = getNumberFormat((session?.searchCount?.toIntOrNull()?:0).toString()), icon1 = R.drawable.ic_search_queries_d,type = GrowthType.SEARCH_QUERIES.name))
    return list
  }

  enum class GrowthType{
    ALL_VISITS, UNIQUE_VISITS,ADDRESS_NEWS,NEWSLETTER_SUBSCRIPTION,SEARCH_QUERIES;
    companion object {
      fun fromName(name: String?): GrowthType? = values().firstOrNull { it.name == name }
    }
  }
}