package com.boost.marketplace.holder.stringHolder

import com.boost.dbcenterapi.recycleritem.*
import com.boost.marketplace.constant.RecyclerViewActionType
import com.boost.marketplace.databinding.SecondaryStringItemBinding

class SampleStringViewHolder(binding: SecondaryStringItemBinding) : AppBaseRecyclerStringHolder<SecondaryStringItemBinding>(binding) {

  override fun bind(position: Int, item: String) {
    super.bind(position, item)

    binding.root.setOnClickListener {
      listener?.onStringItemClick(
        position,
        item,
        RecyclerViewActionType.SECONDARY_IMAGE_CLICK.ordinal
      )
    }
  }
}