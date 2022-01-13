package com.dashboard.controller.ui.more.holder

import android.content.res.ColorStateList
import android.graphics.Color
import com.dashboard.constant.RecyclerViewActionType
import com.dashboard.controller.ui.more.model.UsefulLinksItem
import com.dashboard.databinding.RecyclerItemUsefulLinksBinding
import com.dashboard.model.live.websiteItem.WebsiteActionItem
import com.dashboard.recyclerView.AppBaseRecyclerViewHolder
import com.dashboard.recyclerView.BaseRecyclerViewItem
import com.framework.extensions.invisible
import com.framework.extensions.visible

class UsefulLinksHolder(binding: RecyclerItemUsefulLinksBinding) : AppBaseRecyclerViewHolder<RecyclerItemUsefulLinksBinding>(binding) {

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    val data = item as? UsefulLinksItem
    binding.ctvHeading.text = data?.title
    binding.ctvSubheading.text = data?.subtitle
    if (data?.isActionButtonEnabled == true) {
      binding.ctvNeedsAction.visible()
      binding.ctvNeedsAction.text = data.actionBtn?.title
      if (data.actionBtn?.color.isNullOrEmpty().not() && data.actionBtn?.textColor.isNullOrEmpty().not()) {
        binding.ctvNeedsAction.setTextColor(Color.parseColor(data.actionBtn?.textColor))
        binding.ctvNeedsAction.backgroundTintList = ColorStateList.valueOf((Color.parseColor(data.actionBtn?.color)))
      }
    } else {
      binding.ctvNeedsAction.invisible()
    }
    val iconType = data?.icon?.let { UsefulLinksItem.IconType.fromName(it) }
    iconType?.let { binding.rivIcon.setImageResource(iconType.icon) }
    binding.root.setOnClickListener { listener?.onItemClick(position, item, RecyclerViewActionType.USEFUL_LINKS_CLICK.ordinal) }

  }
}