package com.inventoryorder.holders

import androidx.core.content.ContextCompat
import com.inventoryorder.constant.RecyclerViewActionType
import com.inventoryorder.databinding.ItemBottomSheetPickUpDeliveryOptionBinding
import com.inventoryorder.model.bottomsheet.DeliveryOptionsModel
import com.inventoryorder.recyclerView.AppBaseRecyclerViewHolder
import com.inventoryorder.recyclerView.BaseRecyclerViewItem

class DeliveryOptionsViewHolder(binding: ItemBottomSheetPickUpDeliveryOptionBinding) : AppBaseRecyclerViewHolder<ItemBottomSheetPickUpDeliveryOptionBinding>(binding) {

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    val data = item as DeliveryOptionsModel
    setDataForDeliveryOptions(data)
  }

  private fun setDataForDeliveryOptions(model: DeliveryOptionsModel) {
    model.getIcon()?.let { binding.ivOptionSelected.setImageResource(it) }
    binding.tvOptionSelected.text = model.deliveryOptionSelectedName
    binding.mainView.background = activity?.let { ContextCompat.getDrawable(it, model.getColor()) }
    binding.mainView.setOnClickListener {
      listener?.onItemClick(adapterPosition, model, RecyclerViewActionType.DELIVERY_ITEM_CLICKED.ordinal)
    }
  }

}