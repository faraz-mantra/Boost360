package com.dashboard.model

import com.dashboard.R
import com.dashboard.recyclerView.AppBaseRecyclerViewItem

class DashboardFestiveBanner: AppBaseRecyclerViewItem {
    override fun getViewType(): Int {
        return R.layout.list_item_festive_banner
    }
}