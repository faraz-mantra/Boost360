package com.dashboard.controller.ui.customisationnav.holder

import android.content.res.ColorStateList
import android.graphics.Color
import com.dashboard.constant.RecyclerViewActionType
import com.dashboard.controller.ui.customisationnav.model.WebsiteCustomisationItem
import com.dashboard.databinding.RecyclerItemWebsiteNavBinding
import com.dashboard.recyclerView.AppBaseRecyclerViewHolder
import com.dashboard.recyclerView.BaseRecyclerViewItem
import com.framework.extensions.gone
import com.framework.extensions.visible

class WebsiteNavHolder(binding: RecyclerItemWebsiteNavBinding) : AppBaseRecyclerViewHolder<RecyclerItemWebsiteNavBinding>(binding) {
  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    super.bind(position, item)
    val data = item as? WebsiteCustomisationItem
    binding.recyclerItemAbout.visible()
    binding.ctvHeading.text = data?.title
    binding.ctvSubtitle.text = data?.subtitle
    val iconType = data?.icon?.let { WebsiteCustomisationItem.IconType.fromName(it) }
    iconType?.let { binding.civImage.setImageResource(iconType.icon) }
    binding.root.setOnClickListener { listener?.onItemClick(position, item, RecyclerViewActionType.WEBSITE_NAV_CLICK.ordinal) }
    if (data?.actionBtn != null) {
      binding.ctvNeedsAction.visible()
      binding.ctvNeedsAction.text = data.actionBtn?.title
      binding.ctvNeedsAction.setTextColor(Color.parseColor(data.actionBtn?.textColor))
      binding.ctvNeedsAction.backgroundTintList = ColorStateList.valueOf((Color.parseColor(data.actionBtn?.color)))
    } else {
      binding.ctvNeedsAction.gone()
    }
  }

}
