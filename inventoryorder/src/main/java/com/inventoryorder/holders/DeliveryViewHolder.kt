package com.inventoryorder.holders

import androidx.core.content.ContextCompat
import com.inventoryorder.constant.RecyclerViewActionType
import com.inventoryorder.databinding.ItemBottomSheetPickUpDeliveryOptionBinding
import com.inventoryorder.model.bottomsheet.DeliveryModel
import com.inventoryorder.recyclerView.AppBaseRecyclerViewHolder
import com.inventoryorder.recyclerView.BaseRecyclerViewItem

class DeliveryViewHolder(binding: ItemBottomSheetPickUpDeliveryOptionBinding) : AppBaseRecyclerViewHolder<ItemBottomSheetPickUpDeliveryOptionBinding>(binding) {

    override fun bind(position: Int, item: BaseRecyclerViewItem) {
        super.bind(position, item)
        val data = item as DeliveryModel
        setDataForDeliveryOptions(data)
    }

    private fun setDataForDeliveryOptions(model: DeliveryModel) {
        model.getIcon()?.let { binding.ivOptionSelected.setImageResource(it) }
        binding.tvOptionSelected.text = model.deliveryOptionSelectedName
        binding.mainView.background = activity?.let { ContextCompat.getDrawable(it, model.getColor()) }
        binding.mainView.setOnClickListener {
            listener?.onItemClick(adapterPosition, model, RecyclerViewActionType.DELIVERY_ITEM_CLICKED.ordinal)
        }
    }

}