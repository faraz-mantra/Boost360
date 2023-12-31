package com.boost.marketplace.holder

import com.boost.dbcenterapi.recycleritem.AppBaseRecyclerViewHolder
import com.boost.dbcenterapi.recycleritem.BaseRecyclerViewItem
import com.boost.marketplace.databinding.ItemVideosBinding

class VideosViewHolder(binding:ItemVideosBinding):AppBaseRecyclerViewHolder<ItemVideosBinding>(binding) {

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
  }
}