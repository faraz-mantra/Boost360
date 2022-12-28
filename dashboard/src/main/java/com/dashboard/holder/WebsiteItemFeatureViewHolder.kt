package com.dashboard.holder

import androidx.core.content.ContextCompat
import com.dashboard.R
import com.dashboard.constant.RecyclerViewActionType
import com.dashboard.databinding.ItemWebsiteItemBinding
import com.dashboard.databinding.WebsiteItemFeatureBinding
import com.dashboard.databinding.WebsiteItemFeatureV2Binding
import com.dashboard.model.live.websiteItem.WebsiteActionItem
import com.dashboard.recyclerView.AppBaseRecyclerViewHolder
import com.dashboard.recyclerView.BaseRecyclerViewItem
import com.framework.extensions.invisible
import com.framework.extensions.visible

class WebsiteItemFeatureViewHolder(binding: WebsiteItemFeatureV2Binding) : AppBaseRecyclerViewHolder<WebsiteItemFeatureV2Binding>(binding) {

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    val data = item as? WebsiteActionItem ?: return
    binding.tvCount.text = data.title
    binding.textViewName.text = data.desc
    //ContextCompat.getColorStateList(activity!!, R.color.white)?.let { binding.mainContent.backgroundTintList = it }
    val iconType = data.type?.let { WebsiteActionItem.IconType.fromName(it) }
    binding.ivMainCat.makeGreyscale()
    iconType?.let { binding.ivMainCat.setImageResource(iconType.icon) }
    binding.mainContent.setOnClickListener {
      listener?.onItemClick(position, item, RecyclerViewActionType.WEBSITE_ITEM_CLICK.ordinal)
    }
  }
}
