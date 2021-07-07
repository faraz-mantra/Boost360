package com.dashboard.holder

import com.dashboard.constant.RecyclerViewActionType
import com.dashboard.databinding.ItemRiaAcademyBinding
import com.dashboard.model.live.dashboardBanner.DashboardAcademyBanner
import com.dashboard.recyclerView.AppBaseRecyclerViewHolder
import com.dashboard.recyclerView.BaseRecyclerViewItem
import com.framework.glide.util.glideLoad
import com.inventoryorder.R

class RiaAcademyViewHolder(binding: ItemRiaAcademyBinding) :
  AppBaseRecyclerViewHolder<ItemRiaAcademyBinding>(binding) {

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    val data = item as? DashboardAcademyBanner ?: return
    activity?.glideLoad(binding.image, data.bannerImage?.url ?: "", R.drawable.placeholder_image_n)
    binding.maimView.setOnClickListener {
      listener?.onItemClick(
        position,
        data,
        RecyclerViewActionType.PROMO_BOOST_ACADEMY_CLICK.ordinal
      )
    }
  }
}
