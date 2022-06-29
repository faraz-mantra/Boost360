package com.appservice.holder

import com.appservice.constant.RecyclerViewActionType
import com.appservice.databinding.ItemGstDetailBinding
import com.appservice.model.panGst.GstDetailModel
import com.appservice.recyclerView.AppBaseRecyclerViewHolder
import com.appservice.recyclerView.BaseRecyclerViewItem

class GstDetailViewHolder(binding: ItemGstDetailBinding) :
  AppBaseRecyclerViewHolder<ItemGstDetailBinding>(binding) {

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    val data = item as GstDetailModel
    binding.title.text = "${data.value} %"
    binding.mainView.setOnClickListener {
      listener?.onItemClick(position, data, RecyclerViewActionType.GST_DETAIL_CLICK.ordinal)
    }
  }
}
