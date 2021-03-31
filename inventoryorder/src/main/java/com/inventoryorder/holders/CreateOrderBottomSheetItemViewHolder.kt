package com.inventoryorder.holders

import com.inventoryorder.constant.RecyclerViewActionType
import com.inventoryorder.databinding.BottomSheetOrderOptionBinding
import com.inventoryorder.model.order.orderbottomsheet.BottomSheetOptionsItem
import com.inventoryorder.recyclerView.AppBaseRecyclerViewHolder
import com.inventoryorder.recyclerView.BaseRecyclerViewItem

class CreateOrderBottomSheetItemViewHolder(binding: BottomSheetOrderOptionBinding) : AppBaseRecyclerViewHolder<BottomSheetOrderOptionBinding>(binding) {

    override fun bind(position: Int, item: BaseRecyclerViewItem) {
        super.bind(position, item)
        val data = (item as? BottomSheetOptionsItem) ?: return

        binding.optionTitle.text = data.title
        binding.optionDesc.text = data.description
        binding.radioBtn.isChecked = data.isChecked
        binding.linearLayout.setOnClickListener {
            listener?.onItemClick(adapterPosition, data, RecyclerViewActionType.ORDER_OPTION_SELECTED.ordinal)
        }
        /*binding?.radioBtn?.setOnCheckedChangeListener { p0, p1 ->
        }*/
    }
}