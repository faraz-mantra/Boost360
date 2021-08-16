package com.inventoryorder.holders

import com.inventoryorder.constant.ActionType
import com.inventoryorder.databinding.ItemConsultationServicesBinding
import com.inventoryorder.model.services.InventoryServicesResponseItem
import com.inventoryorder.recyclerView.AppBaseRecyclerViewHolder
import com.inventoryorder.recyclerView.BaseRecyclerViewItem

class ServicesViewHolder(binding: ItemConsultationServicesBinding) :
  AppBaseRecyclerViewHolder<ItemConsultationServicesBinding>(binding) {
  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    val data = (item as? InventoryServicesResponseItem) ?: return
    binding.serviceIcon.setImageResource(data.getIcon())
    binding.serviceName.text = data.name
    binding.mainContent.setOnClickListener {
      listener?.onItemClick(
        position,
        data,
        ActionType.CHECK_SERVICE_CLICK.ordinal
      )
    }
  }
}