package com.boost.marketplace.holder

import com.boost.dbcenterapi.recycleritem.AppBaseRecyclerViewHolder
import com.boost.dbcenterapi.recycleritem.BaseRecyclerViewItem
import com.boost.marketplace.constant.RecyclerViewActionType
import com.boost.marketplace.databinding.ItemPacksBinding
import com.boost.marketplace.databinding.SecondaryImageItemBinding

class SecondaryImagesViewHolder(binding: SecondaryImageItemBinding) : AppBaseRecyclerViewHolder<SecondaryImageItemBinding>(binding) {

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)

    binding.root.setOnClickListener {
      listener?.onItemClick(
        position,
        item,
        RecyclerViewActionType.SECONDARY_IMAGE_CLICK.ordinal
      )
    }
  }
}