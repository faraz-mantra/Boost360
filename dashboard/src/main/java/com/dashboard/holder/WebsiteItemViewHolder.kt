package com.dashboard.holder

import androidx.core.content.ContextCompat
import com.dashboard.R
import com.dashboard.constant.RecyclerViewActionType
import com.dashboard.databinding.ItemWebsiteItemBinding
import com.dashboard.model.live.websiteItem.WebsiteActionItem
import com.dashboard.recyclerView.AppBaseRecyclerViewHolder
import com.dashboard.recyclerView.BaseRecyclerViewItem

class WebsiteItemViewHolder(binding: ItemWebsiteItemBinding) :
  AppBaseRecyclerViewHolder<ItemWebsiteItemBinding>(binding) {

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    val data = item as? WebsiteActionItem ?: return
    binding.textViewName.text = data.title
    binding.txtDesc.text = data.desc
    ContextCompat.getColorStateList(
      activity!!,
      if (position % 2 != 0) R.color.white_smoke_1 else R.color.white
    )?.let { binding.mainContent.backgroundTintList = it }
    val iconType = data.type?.let { WebsiteActionItem.IconType.fromName(it) }
    iconType?.let { binding.imageViewIcon.setImageResource(iconType.icon) }
    binding.mainContent.setOnClickListener {
      listener?.onItemClick(
        position,
        item,
        RecyclerViewActionType.WEBSITE_ITEM_CLICK.ordinal
      )
    }
  }
}
