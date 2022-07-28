package com.dashboard.holder

import androidx.core.content.ContextCompat
import com.dashboard.R
import com.dashboard.constant.RecyclerViewActionType
import com.dashboard.databinding.ItemWebsiteItemBinding
import com.dashboard.databinding.ItemWebsiteItemV2Binding
import com.dashboard.model.live.websiteItem.WebsiteActionItem
import com.dashboard.recyclerView.AppBaseRecyclerViewHolder
import com.dashboard.recyclerView.BaseRecyclerViewItem
import com.framework.extensions.gone
import com.framework.extensions.invisible
import com.framework.extensions.visible
import com.framework.utils.capitalized

class WebsiteItemViewHolder(binding: ItemWebsiteItemV2Binding) : AppBaseRecyclerViewHolder<ItemWebsiteItemV2Binding>(binding) {

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    val data = item as? WebsiteActionItem ?: return
    binding.textViewName.text = data.title?.split(' ')?.joinToString(" ") { it.capitalized() }
    binding.txtDesc.text = data.desc
    binding.tvCount.text = data.getCountN().toString()
    getColor(if (data.getCountN() == 0) R.color.gray_e2e2e2e2 else R.color.black_4a4a4a)?.let { binding.tvCount.setTextColor(it) }
    ContextCompat.getColorStateList(activity!!, R.color.white)?.let { binding.mainContent.backgroundTintList = it }
    val iconType = data.type?.let { WebsiteActionItem.IconType.fromName(it) }
    iconType?.let { binding.ivMainCat.setImageResource(iconType.icon) }
    binding.ivMainCat.makeGreyscale()
   /* if (data.getCountN() == 0){
      binding.ivAdd.visible()
      binding.ivForwardArrow.gone()
    }else{
      binding.ivAdd.gone()
      binding.ivForwardArrow.visible()
    }*/

    binding.mainContent.setOnClickListener {
      listener?.onItemClick(position, item, RecyclerViewActionType.WEBSITE_ITEM_CLICK.ordinal)
    }
    binding.ivAdd.setOnClickListener {
      listener?.onItemClick(position, item, RecyclerViewActionType.WEBSITE_CONTENT_ADD_CLICK.ordinal)
    }
  }
}
