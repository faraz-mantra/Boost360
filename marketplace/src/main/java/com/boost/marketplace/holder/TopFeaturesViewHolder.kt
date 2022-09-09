package com.boost.marketplace.holder

import com.boost.dbcenterapi.recycleritem.AppBaseRecyclerViewHolder
import com.boost.dbcenterapi.recycleritem.BaseRecyclerViewItem
import com.boost.marketplace.constant.RecyclerViewActionType
import com.boost.marketplace.databinding.ItemPacksListBinding

class TopFeaturesViewHolder(binding: ItemPacksListBinding) : AppBaseRecyclerViewHolder<ItemPacksListBinding>(binding) {

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    binding.root.setOnClickListener {
      listener?.onItemClick(
        position,
        item,
        RecyclerViewActionType.TOP_FEATURES_CLICK.ordinal
      )
    }
  }
}