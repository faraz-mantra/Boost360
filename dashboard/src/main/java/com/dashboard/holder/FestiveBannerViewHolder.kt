package com.dashboard.holder

import com.dashboard.constant.RecyclerViewActionType
import com.dashboard.databinding.ItemBoostPremiumBinding
import com.dashboard.databinding.ListItemFestiveBannerBinding
import com.dashboard.model.DashboardFestiveBanner
import com.dashboard.model.live.dashboardBanner.DashboardMarketplaceBanner
import com.dashboard.recyclerView.AppBaseRecyclerViewHolder
import com.dashboard.recyclerView.BaseRecyclerViewItem
import com.framework.glide.util.glideLoad
import com.inventoryorder.R

class FestiveBannerViewHolder(binding: ListItemFestiveBannerBinding) :
  AppBaseRecyclerViewHolder<ListItemFestiveBannerBinding>(binding) {

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    val data = item as? DashboardFestiveBanner ?: return

    binding.root.setOnClickListener {
      listener?.onItemClick(position,item,RecyclerViewActionType.FESTIVE_BANNER_CLICK.ordinal)
    }
  }

}
