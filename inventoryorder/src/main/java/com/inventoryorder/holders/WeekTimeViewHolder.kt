package com.inventoryorder.holders

import com.framework.extensions.gone
import com.framework.extensions.visible
import com.inventoryorder.databinding.ItemWeekTimeSelectBinding
import com.inventoryorder.model.services.TimingModel
import com.inventoryorder.recyclerView.AppBaseRecyclerViewHolder
import com.inventoryorder.recyclerView.AppBaseRecyclerViewItem
import com.inventoryorder.recyclerView.BaseRecyclerViewItem

class WeekTimeViewHolder(binding: ItemWeekTimeSelectBinding):AppBaseRecyclerViewHolder<ItemWeekTimeSelectBinding>(binding){
    override fun bind(position: Int, item: BaseRecyclerViewItem) {
        super.bind(position, item)
        val data = item as? TimingModel ?: return
        if (data.isMarkOff) {
            binding.tvMarkOff.visible()
            binding.tvAvailableDate.gone()
            binding.tvMarkOff.text = data.getIsMarkOffText()
        } else {
            binding.tvMarkOff.gone()
            binding.tvAvailableDate.visible()
            binding.tvAvailableDate.text = data.getIsMarkOnText()
        }
        binding.executePendingBindings()
    }
}