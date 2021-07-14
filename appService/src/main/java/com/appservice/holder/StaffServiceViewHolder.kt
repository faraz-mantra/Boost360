package com.appservice.holder

import com.appservice.constant.RecyclerViewActionType
import com.appservice.databinding.RecyclerItemServiceBinding
import com.appservice.recyclerView.AppBaseRecyclerViewHolder
import com.appservice.recyclerView.BaseRecyclerViewItem
import com.appservice.model.staffModel.DataItemService

class StaffServiceViewHolder(binding: RecyclerItemServiceBinding) :
  AppBaseRecyclerViewHolder<RecyclerItemServiceBinding>(binding) {
  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    val data = item as DataItemService
    binding.ccbServices.text = "${data.name}"
    binding.ccbServices.isChecked = data.isChecked ?: false
    binding.ccbServices.setOnClickListener {
      listener?.onItemClick(
        position,
        data,
        RecyclerViewActionType.SERVICE_ITEM_CLICK.ordinal
      )
    }
  }

}