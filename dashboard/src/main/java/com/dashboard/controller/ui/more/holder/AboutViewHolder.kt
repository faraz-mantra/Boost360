package com.dashboard.controller.ui.more.holder

import com.dashboard.constant.RecyclerViewActionType
import com.dashboard.controller.ui.more.model.AboutAppSectionItem
import com.dashboard.controller.ui.more.model.UsefulLinksItem
import com.dashboard.databinding.ItemBoostAddOnsBinding
import com.dashboard.databinding.RecyclerItemAboutAppBinding
import com.dashboard.recyclerView.AppBaseRecyclerViewHolder
import com.dashboard.recyclerView.BaseRecyclerViewItem

class AboutViewHolder(binding: RecyclerItemAboutAppBinding)
  :AppBaseRecyclerViewHolder<RecyclerItemAboutAppBinding>(binding) {
  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    val data = item as? AboutAppSectionItem
    binding.ctvHeading.text = data?.title
    binding.ctvSubtitle.text = data?.subtitle
    val iconType = data?.icon?.let { AboutAppSectionItem.IconType.fromName(it) }
    iconType?.let { binding.civImage.setImageResource(iconType.icon) }
    binding.root.setOnClickListener { listener?.onItemClick(position,item,RecyclerViewActionType.ABOUT_VIEW_CLICK.ordinal) }
  }

}
